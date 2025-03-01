import atexit
import unittest
from pact import Consumer, Provider


class ProductServiceContractTest(unittest.TestCase):
    def setUp(self):
        self.pact = Consumer("order-service").has_pact_with(
            Provider("product-service"), pact_dir="./pacts"
        )
        self.pact.start_service()
        atexit.register(self.pact.stop_service)

        def test_get_product_exists(self):
            expected = {
                "productId": "1",
                "name": "Test Product",
                "description": "Test Description",
                "price": 99.99,
                "category": "Electronics",
                "stockQuantity": 10,
            }

            (
                self.pact.given("a product with ID 1 exists")
                .upon_receiving("a request for product 1")
                .with_request("GET", "/api/products/1")
                .will_respond_with(200, body=expected)
            )

            with self.pact:
                # Your actual API client call would go here
                pass

        def test_get_product_not_found(self):
            (
                self.pact.given("no product with ID 999 exists")
                .upon_receiving("a request for non-existent product")
                .with_request("GET", "/api/products/999")
                .will_respond_with(404)
            )

            with self.pact:
                # Your actual API client call would go here
                pass


if __name__ == "__main__":
    unittest.main()
