from datetime import datetime
from typing import List
from pydantic import BaseModel
from .order import OrderItem, OrderStatus


class OrderCreatedEvent(BaseModel):
    orderId: str
    userId: str
    totalAmount: float
    items: List[OrderItem]
    createdAt: datetime


class OrderStatusChangedEvent(BaseModel):
    orderId: str
    newStatus: OrderStatus
    oldStatus: OrderStatus
    updatedAt: datetime


class PaymentProcessedEvent(BaseModel):
    orderId: str
    paymentId: str
    amount: float
    processedAt: datetime
