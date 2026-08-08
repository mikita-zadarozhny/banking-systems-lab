# banking-systems-lab
A distributed banking-system laboratory focused on ledger correctness, transactional workflows, observability, resilience, and performance under load.

## Context
Each payment has its own lifecycle. In order to complete, a payment gets through multiple 
layers, namely through the following ones:
- Point-of-sale (POS).
- Payment Gateway.
- Payment Processor.
- Acquiring Bank.
- Card Network.
- Issuing Bank.

Diagrams of the aforementioned lifecycle is presebt below:

![payment-lifecycle.png](docs/payment-lifecycle.png)