from enum import Enum
from typing import List
from datetime import datetime
from beanie import Document
from pydantic import BaseModel


class OrderStatus(str, Enum):
    PENDING = "PENDING"
    CONFIRMED = "CONFIRMED"
    SHIPPED = "SHIPPED"
    DELIVERED = "DELIVERED"
    CANCELLED = "CANCELLED"


class OrderItem(BaseModel):
    productId: str
    quantity: int


class Order(Document):
    userId: str
    items: List[OrderItem]
    status: OrderStatus
    totalAmount: float
    createdAt: datetime
    updatedAt: datetime

    class Settings:
        name = "orders"
