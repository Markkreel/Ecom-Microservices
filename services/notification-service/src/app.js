const express = require("express");
const mongoose = require("mongoose");
const subscriptionRoutes = require("./routes/subscription.routes");
const notificationRoutes = require("./routes/notification.routes");

const app = express();

app.use(express.json());

// Routes
app.use("/api/subscriptions", subscriptionRoutes);
app.use("/api/notifications", notificationRoutes);

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({
    success: false,
    error: err.message || "Internal Server Error",
  });
});

// Connect to MongoDB if not in test environment
if (process.env.NODE_ENV !== "test") {
  mongoose
    .connect(
      process.env.MONGODB_URI ||
        "mongodb://localhost:27017/notification-service"
    )
    .then(() => console.log("Connected to MongoDB"))
    .catch((err) => console.error("MongoDB connection error:", err));
}

// Start server if not being required as a module
if (require.main === module) {
  const PORT = process.env.PORT || 3000;
  app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
  });
}

module.exports = app;
