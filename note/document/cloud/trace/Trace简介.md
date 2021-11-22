---
begin: 2021-11-22
status: ongoing
rating: 1
---

# Trace简介

Traces track the progression of a single request, called a **trace**, as it is handled by services that make up an application. The request may be initiated by a user or an application. Distributed tracing is a form of tracing that traverses process, network and security boundaries. Each unit of work in a trace is called a **span**; a trace is a tree of spans. Spans are objects that represent the work being done by individual services or components involved in a request as it flows through a system. A span contains a _span context_, which is a set of globally unique identifiers that represent the unique request that each span is a part of. A span provides Request, Error and Duration (RED) metrics that can be used to debug availability as well as performance issues.

A trace contains a single _root span_ which encapsulates the end-to-end latency for the entire request. You can think of this as a single logical operation, such as clicking a button in a web application to add a product to a shopping cart. The _root span_ would measure the time it took from an end-user clicking that button to the operation being completed or failing (so, the item is added to the cart or some error occurs) and the result being displayed to the user. A trace is comprised of the single _root span_ and any number of _child spans_, which represent operations taking place as part of the request. Each span contains metadata about the operation, such as its name, start and end timestamps, attributes, events, and status.

To create and manage spans in OpenTelemetry, the OpenTelemetry API provides the `tracer` interface. This object is responsible for tracking the active span in your process, and allows you to access the current span in order to perform operations on it such as adding attributes, events, and finishing it when the work it tracks is complete. One or more `tracer` objects can be created in a process through the _tracer provider_, a factory interface that allows for multiple tracers to be instantiated in a single process with different options.

Generally, the lifecycle of a span resembles the following:

-   A request is received by a service. The span context is _extracted_ from the request headers, if it exists.
-   A new span is created as a child of the extracted span context; if none exists, a new root span is created.
-   The service handles the request. Additional attributes and events are added to the span that are useful for understanding the context of the request, such as the hostname of the machine handling the request, or customer identifiers.
-   New spans may be created to represent work being done by sub-components of the service.
-   When the service makes a remote call to another service, the current span context is serialized and forwarded to the next service by _injecting_ the span context into the headers or message envelope.
-   The work being done by the service completes, successfully or not. The span status is appropriately set, and the span is marked finished.

For more information, see the [traces specification](https://opentelemetry.io/docs/reference/specification/overview/#tracing-signal), which covers concepts including: trace, span, parent/child relationship, span context, attributes, events and links.

## 参考链接


##### 标签
