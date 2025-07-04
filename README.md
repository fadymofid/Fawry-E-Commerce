# E‑Commerce System Demo

A simple Java console application showcasing a basic e-commerce flow with cart management, shipping calculation, and checkout, following key OOP and SOLID principles.

## Overview

* **Products**: Two types:

  * *Perishable* (e.g., Cheese, Biscuits) with expiration date and weight.
  * *Non-Perishable* (e.g., TV, Mobile Scratch Card) with optional shipping weight.
* **Cart**: Add items by quantity (must not exceed stock or be expired).
* **Shipping**: Collects all shippable items (those with weight) and calculates fee based on total weight.
* **Checkout**: Validates cart, computes subtotal + shipping, deducts customer balance, updates stock, and prints receipt or error.

## How It Works

1. **Models** (`Models` package)

   * `Product` (abstract): name, price, quantity, abstract methods `isExpired()` and `requiresShipping()`.
   * `PerishableProduct`: implements expiration check and always requires shipping.
   * `NonPerishableProduct`: may or may not require shipping, provides weight.
   * `CartItem`, `ShoppingCart`: manage items, validate quantity and expiration.
   * `Customer`: holds balance, deducts payment.

2. **ShippingService** (`Services` package)

   * **Interface**: Defines `processShipment()` that returns shipping fee and weight.
   * **DefaultShippingService**: Implements fee = base fee + (total weight × rate).

3. **Adapter Pattern**

   * `NonPerishableShippableAdapter`: wraps a non-perishable product to fit the `Shippable` interface (name + weight).

4. **ECommerceSystem**

   * **Dependency Injection**: Accepts a `ShippingService` instance.
   * **Checkout Flow** split into small methods:

     * *validateCart()*: empty, out-of-stock, expired checks.
     * *collectShippables()*: gather items for shipping.
     * *processPayment()*: deduct customer balance.
     * *updateInventory()*: subtract purchased quantities.
     * *printReceipt()*: show items, subtotal, shipping, total, remaining balance.



This simple structure demonstrates encapsulation, single responsibility, open/closed extension, and dependency inversion in a minimal Java app.
