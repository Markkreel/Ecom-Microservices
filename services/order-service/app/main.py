from fastapi import FastAPI, Depends, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from typing import Optional
from beanie import init_beanie
from motor.motor_asyncio import AsyncIOMotorClient
from datetime import datetime
from .models.order import Order, OrderStatus
from .models.request import CreateOrderRequest
from .models.response import OrderResponse, PagedOrderResponse
from .services.auth import get_current_user
from .services.product import ProductService
from .utils.config import Settings

app = FastAPI(
    title="Order Service",
    description="Order management for e-commerce microservices",
    version="1.0.0",
)

# CORS middleware configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Database initialization
@app.on_event("startup")
async def init_db():
    settings = Settings()
    client = AsyncIOMotorClient(settings.mongodb_url)
    await init_beanie(database=client[settings.mongodb_db], document_models=[Order])


# Create a new order
@app.post("/api/v1/orders", response_model=OrderResponse)
async def create_order(
    request: CreateOrderRequest, current_user: dict = Depends(get_current_user)
):
    # Validate products and calculate total amount
    product_service = ProductService()
    total_amount = 0

    for item in request.items:
        product = await product_service.get_product(item.productId)
        if not product:
            raise HTTPException(
                status_code=404, detail=f"Product {item.productId} not found"
            )
        if product.stockQuantity < item.quantity:
            raise HTTPException(
                status_code=400,
                detail=f"Insufficient stock for product {item.productId}",
            )
        total_amount += product.price * item.quantity

    # Create order
    order = Order(
        userId=current_user["userId"],
        items=request.items,
        status=OrderStatus.PENDING,
        totalAmount=total_amount,
        createdAt=datetime.utcnow(),
        updatedAt=datetime.utcnow(),
    )
    await order.insert()

    return OrderResponse.from_order(order)


# Get orders for authenticated user
@app.get("/api/v1/orders", response_model=PagedOrderResponse)
async def get_orders(
    status: Optional[OrderStatus] = None,
    page: int = Query(1, ge=1),
    size: int = Query(10, ge=1, le=100),
    current_user: dict = Depends(get_current_user),
):
    skip = (page - 1) * size
    query = {"userId": current_user["userId"]}
    if status:
        query["status"] = status

    total = await Order.find(query).count()
    orders = await Order.find(query).skip(skip).limit(size).to_list()

    return PagedOrderResponse(
        items=[OrderResponse.from_order(order) for order in orders],
        totalItems=total,
        totalPages=(total + size - 1) // size,
    )


# Get order by ID
@app.get("/api/v1/orders/{orderId}", response_model=OrderResponse)
async def get_order(orderId: str, current_user: dict = Depends(get_current_user)):
    order = await Order.get(orderId)
    if not order or order.userId != current_user["userId"]:
        raise HTTPException(status_code=404, detail="Order not found")

    return OrderResponse.from_order(order)
