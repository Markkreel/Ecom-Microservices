const { expect } = require('chai');
const sinon = require('sinon');
const { NotificationController } = require('../../../src/controllers/notification.controller');
const { NotificationService } = require('../../../src/services/notification.service');

describe('NotificationController', () => {
    let notificationController;
    let notificationService;

    beforeEach(() => {
        notificationService = {
            sendNotification: sinon.stub(),
            getNotificationHistory: sinon.stub()
        };
        notificationController = new NotificationController(notificationService);
    });

    afterEach(() => {
        sinon.restore();
    });

    describe('sendNotification', () => {
        it('should successfully send a notification', async () => {
            const mockReq = {
                body: {
                    userId: '123',
                    type: 'EMAIL',
                    message: 'Test notification'
                }
            };
            const mockRes = {
                status: sinon.stub().returnsThis(),
                json: sinon.stub()
            };

            notificationService.sendNotification.resolves({
                id: '456',
                status: 'SENT'
            });

            await notificationController.sendNotification(mockReq, mockRes);

            expect(mockRes.status.calledWith(200)).to.be.true;
            expect(mockRes.json.calledWith({
                success: true,
                data: {
                    id: '456',
                    status: 'SENT'
                }
            })).to.be.true;
        });

        it('should handle errors when sending notification fails', async () => {
            const mockReq = {
                body: {
                    userId: '123',
                    type: 'EMAIL',
                    message: 'Test notification'
                }
            };
            const mockRes = {
                status: sinon.stub().returnsThis(),
                json: sinon.stub()
            };

            const error = new Error('Failed to send notification');
            notificationService.sendNotification.rejects(error);

            await notificationController.sendNotification(mockReq, mockRes);

            expect(mockRes.status.calledWith(500)).to.be.true;
            expect(mockRes.json.calledWith({
                success: false,
                error: 'Failed to send notification'
            })).to.be.true;
        });
    });

    describe('getNotificationHistory', () => {
        it('should return notification history for a user', async () => {
            const mockReq = {
                params: {
                    userId: '123'
                }
            };
            const mockRes = {
                status: sinon.stub().returnsThis(),
                json: sinon.stub()
            };

            const mockHistory = [
                { id: '1', type: 'EMAIL', status: 'SENT' },
                { id: '2', type: 'SMS', status: 'SENT' }
            ];

            notificationService.getNotificationHistory.resolves(mockHistory);

            await notificationController.getNotificationHistory(mockReq, mockRes);

            expect(mockRes.status.calledWith(200)).to.be.true;
            expect(mockRes.json.calledWith({
                success: true,
                data: mockHistory
            })).to.be.true;
        });
    });
}));