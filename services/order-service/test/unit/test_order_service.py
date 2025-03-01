from datetime import datetime
from unittest.mock import AsyncMock, patch
import pytest
from fastapi import HTTPException
from app.models.order import Order, OrderStatus
from app.models.request import CreateOrderRequest, OrderItem
from app.models.response import OrderResponse, PagedOrderResponse
from app.main import create_order, get_orders
from app.services.product import ProductService


@pytest.fixture
def mock_current_user():
    return {"userId": "test_user_id"}


@pytest.fixture
def mock_product_service():
    async def get_product(product_id):
        if product_id == "999":
            return None
        return type(
            "Product",
            (),
            {"productId": product_id, "price": 100.0, "stockQuantity": 10},
        )

    with patch.object(ProductService, "get_product", new_callable=AsyncMock) as mock:
        mock.side_effect = get_product
        yield mock


@pytest.fixture
def mock_event_publisher():
    with patch(
        "app.main.event_publisher.publish_order_created", new_callable=AsyncMock
    ) as mock:
        yield mock


@pytest.fixture
def mock_order():
    async def mock_insert(self):
        self.id = "test_order_id"
        return self

    with patch.object(Order, "insert", new_callable=AsyncMock) as mock:
        mock.side_effect = mock_insert
        yield mock


@pytest.mark.asyncio
async def test_create_order_success(current_user, mock_publisher):
    request = CreateOrderRequest(items=[OrderItem(productId="1", quantity=2)])

    response = await create_order(request, current_user)

    assert isinstance(response, OrderResponse)
    assert response.userId == current_user["userId"]
    assert response.status == OrderStatus.PENDING
    assert response.totalAmount == 200.0  # 2 items * $100
    mock_publisher.assert_called_once()


@pytest.mark.asyncio
async def test_create_order_product_not_found(test_user):
    request = CreateOrderRequest(items=[OrderItem(productId="999", quantity=1)])

    with pytest.raises(HTTPException) as exc_info:
        await create_order(request, test_user)

    assert exc_info.value.status_code == 404
    assert "Product 999 not found" in str(exc_info.value.detail)


@pytest.mark.asyncio
async def test_create_order_insufficient_stock(test_user):
    request = CreateOrderRequest(
        items=[OrderItem(productId="1", quantity=15)]
    )  # More than available stock

    with pytest.raises(HTTPException) as exc_info:
        await create_order(request, test_user)

    assert exc_info.value.status_code == 400
    assert "Insufficient stock" in str(exc_info.value.detail)


@pytest.mark.asyncio
async def test_get_orders():
    # Mock Order.find() query builder
    mock_orders = [
        Order(
            id="1",
            userId=mock_current_user["userId"],
            items=[OrderItem(productId="1", quantity=1)],
            status=OrderStatus.PENDING,
            totalAmount=100.0,
            createdAt=datetime.utcnow(),
            updatedAt=datetime.utcnow(),
        )
    ]

    with patch("app.models.order.Order.find") as mock_find:
        mock_find.return_value.count = AsyncMock(return_value=1)
        mock_find.return_value.skip = lambda x: mock_find.return_value
        mock_find.return_value.limit = lambda x: mock_find.return_value
        mock_find.return_value.to_list = AsyncMock(return_value=mock_orders)

        response = await get_orders(
            status=OrderStatus.PENDING, page=1, size=10, current_user=mock_current_user
        )

        assert isinstance(response, PagedOrderResponse)
        assert len(response.items) == 1
        assert response.totalItems == 1
        assert response.totalPages == 1
        assert response.items[0].userId == mock_current_user["userId"]
        assert response.items[0].status == OrderStatus.PENDING
