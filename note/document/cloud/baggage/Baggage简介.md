---
begin: 2021-11-22
status: ongoing
rating: 1
---

# Baggage简介

## Baggage[](https://opentelemetry.io/docs/concepts/data-sources/#baggage)

In addition to trace propagation, OpenTelemetry provides a simple mechanism for propagating name/value pairs, called **baggage**. Baggage is intended for indexing observability events in one service with attributes provided by a prior service in the same transaction. This helps to establish a causal relationship between these events.

While baggage can be used to prototype other cross-cutting concerns, this mechanism is primarily intended to convey values for the OpenTelemetry observability systems.

These values can be consumed from baggage and used as additional dimensions for metrics, or additional context for logs and traces. Some examples:

-   A web service can benefit from including context around what service has sent the request
-   A SaaS provider can include context about the API user or token that is responsible for that request
-   Determining that a particular browser version is associated with a failure in an image processing service

For more information, see the [baggage specification](https://opentelemetry.io/docs/reference/specification/overview/#baggage-signal).

## 参考链接


##### 标签
#cloud-native 