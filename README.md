# banking-systems-lab
A distributed banking-system laboratory focused on ledger correctness, transactional workflows, observability, resilience, and performance under load.

## Context

### Payment Processing

Each payment has its own lifecycle. In order to complete, a payment gets through multiple
layers, namely through the following ones:
- Point-of-sale (POS).
- Payment Gateway.
- Payment Processor.
- Acquiring Bank.
- Card Network.
- Issuing Bank.

![payment-processing.png](docs/payment-processing.png)

### Payment Settlement
Once per some time interval, a merchant run settlement operation, which sends all the gathered
authorized transactions and send them for settlement at merchant's bank account.

![payment-settlement.png](docs/payment-settlement.png)

### Payment Gateway
Payment gateway is a bridge between point-of-sale (POS) and payment processors. Some systems
might serve as payment gateway and processor the same time. But let's separate these two
concepts for clarity.

Let's first take a look at sequence diagram for each payment that gets through payment gateway:
![payment-gateway-sequence-diagram.png](docs/payment-gateway-sequence-diagram.png)

High level architecture is present below:
![payment-gateway-high-level-architecture.png](docs/payment-gateway-high-level-architecture.png)

Database cluster setup, ensuring strict consistency and durability, is present below:
![payment-gateway-payment-service-database-cluster.png](docs/payment-gateway-payment-service-database-cluster.png)