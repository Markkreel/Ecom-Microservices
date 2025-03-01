const mongoose = require("mongoose");
const { MongoMemoryServer } = require("mongodb-memory-server");
const request = require("supertest");
const app = require("../../src/app");
const Subscription = require("../../src/models/subscription");
const Notification = require("../../src/models/notification");

describe("Notification Service Integration Tests", () => {
  let mongoServer;

  beforeAll(async () => {
    mongoServer = await MongoMemoryServer.create();
    const mongoUri = mongoServer.getUri();
    await mongoose.connect(mongoUri);
  });

  afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
  });

  beforeEach(async () => {
    await Subscription.deleteMany({});
    await Notification.deleteMany({});
  });

  describe("Subscription Management", () => {
    test("should create user subscription preferences", async () => {
      const userId = "test-user-123";
      const subscriptionData = {
        userId,
        channels: ["email", "push"],
        preferences: {
          orderUpdates: true,
          promotions: false,
          account: true,
        },
      };

      const response = await request(app)
        .post("/api/subscriptions")
        .send(subscriptionData)
        .expect(201);

      const subscription = await Subscription.findOne({ userId });
      expect(subscription).toBeTruthy();
      expect(subscription.channels).toEqual(
        expect.arrayContaining(["email", "push"])
      );
      expect(subscription.preferences.promotions).toBe(false);
    });

    test("should update user subscription preferences", async () => {
      const userId = "test-user-123";
      const subscription = await Subscription.create({
        userId,
        channels: ["email"],
        preferences: {
          orderUpdates: true,
          promotions: true,
          account: true,
        },
      });

      const updateData = {
        channels: ["email", "sms"],
        preferences: {
          promotions: false,
        },
      };

      await request(app)
        .patch(`/api/subscriptions/${userId}`)
        .send(updateData)
        .expect(200);

      const updatedSubscription = await Subscription.findOne({ userId });
      expect(updatedSubscription.channels).toEqual(
        expect.arrayContaining(["email", "sms"])
      );
      expect(updatedSubscription.preferences.promotions).toBe(false);
    });
  });

  describe("Notification Handling", () => {
    test("should create and send notification based on user preferences", async () => {
      const userId = "test-user-123";
      await Subscription.create({
        userId,
        channels: ["email"],
        preferences: {
          orderUpdates: true,
        },
      });

      const notificationData = {
        userId,
        type: "orderUpdates",
        channel: "email",
        content: {
          orderId: "order-123",
          status: "shipped",
        },
      };

      const response = await request(app)
        .post("/api/notifications")
        .send(notificationData)
        .expect(201);

      const notification = await Notification.findOne({ userId });
      expect(notification).toBeTruthy();
      expect(notification.status).toBe("pending");
      expect(notification.type).toBe("orderUpdates");
    });

    test("should not send notification for unsubscribed channel", async () => {
      const userId = "test-user-123";
      await Subscription.create({
        userId,
        channels: ["email"],
        preferences: {
          orderUpdates: true,
        },
      });

      const notificationData = {
        userId,
        type: "orderUpdates",
        channel: "sms",
        content: {
          orderId: "order-123",
          status: "shipped",
        },
      };

      await request(app)
        .post("/api/notifications")
        .send(notificationData)
        .expect(400);

      const notification = await Notification.findOne({ userId });
      expect(notification).toBeFalsy();
    });
  });
});
