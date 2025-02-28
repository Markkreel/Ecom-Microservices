from typing import Any
from datetime import datetime
from ..models.events import (
    OrderCreatedEvent,
    OrderStatusChangedEvent,
    PaymentProcessedEvent,
)
from ..models.order import Order, OrderStatus


class EventPublisher:
    def __init__(self):
        # TODO: Initialize message broker client
        pass

    async def publish_event(self, topic: str, event: Any) -> None:
        # TODO: Implement actual message publishing logic
        print(f"Publishing event to {topic}: {event.dict()}")

    async def publish_order_created(self, order: Order) -> None:
        event = OrderCreatedEvent(
            orderId=str(order.id),
            userId=order.userId,
            totalAmount=order.totalAmount,
            items=order.items,
            createdAt=order.createdAt,
        )
        await self.publish_event("order.created", event)

    async def publish_order_status_changed(
        self, order: Order, old_status: OrderStatus
    ) -> None:
        event = OrderStatusChangedEvent(
            orderId=str(order.id),
            newStatus=order.status,
            oldStatus=old_status,
            updatedAt=order.updatedAt,
        )
        await self.publish_event("order.status_changed", event)

    async def publish_payment_processed(self, order: Order, payment_id: str) -> None:
        event = PaymentProcessedEvent(
            orderId=str(order.id),
            paymentId=payment_id,
            amount=order.totalAmount,
            processedAt=datetime.utcnow(),
        )
        await self.publish_event("order.payment_processed", event)
