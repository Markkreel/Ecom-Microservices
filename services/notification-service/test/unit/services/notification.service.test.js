const { expect } = require("chai");
const sinon = require("sinon");
const {
  NotificationService,
} = require("../../../src/services/notification.service");
const { NotificationModel } = require("../../../src/models/notification.model");

describe("NotificationService", () => {
  let notificationService;
  let notificationModel;

  beforeEach(() => {
    notificationModel = {
      create: sinon.stub(),
      find: sinon.stub(),
      findById: sinon.stub(),
    };
    notificationService = new NotificationService(notificationModel);
  });

  afterEach(() => {
    sinon.restore();
  });

  describe("sendNotification", () => {
    it("should successfully create and send a notification", async () => {
      const notificationData = {
        userId: "123",
        type: "EMAIL",
        message: "Test notification",
      };

      const createdNotification = {
        id: "456",
        ...notificationData,
        status: "SENT",
        createdAt: new Date(),
      };

      notificationModel.create.resolves(createdNotification);

      const result = await notificationService.sendNotification(
        notificationData
      );

      expect(result).to.deep.equal(createdNotification);
      expect(notificationModel.create.calledOnce).to.be.true;
      expect(notificationModel.create.firstCall.args[0]).to.deep.equal({
        ...notificationData,
        status: "SENT",
      });
    });

    it("should handle errors when creating notification fails", async () => {
      const notificationData = {
        userId: "123",
        type: "EMAIL",
        message: "Test notification",
      };

      const error = new Error("Database error");
      notificationModel.create.rejects(error);

      try {
        await notificationService.sendNotification(notificationData);
        expect.fail("Should have thrown an error");
      } catch (err) {
        expect(err).to.equal(error);
      }
    });
  });

  describe("getNotificationHistory", () => {
    it("should return notification history for a user", async () => {
      const userId = "123";
      const mockNotifications = [
        { id: "1", userId, type: "EMAIL", status: "SENT", message: "Test 1" },
        { id: "2", userId, type: "SMS", status: "SENT", message: "Test 2" },
      ];

      notificationModel.find.resolves(mockNotifications);

      const result = await notificationService.getNotificationHistory(userId);

      expect(result).to.deep.equal(mockNotifications);
      expect(notificationModel.find.calledOnce).to.be.true;
      expect(notificationModel.find.firstCall.args[0]).to.deep.equal({
        userId,
      });
    });

    it("should return empty array when no notifications found", async () => {
      const userId = "123";
      notificationModel.find.resolves([]);

      const result = await notificationService.getNotificationHistory(userId);

      expect(result).to.deep.equal([]);
      expect(notificationModel.find.calledOnce).to.be.true;
    });
  });
});
