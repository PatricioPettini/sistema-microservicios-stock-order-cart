🛍️ Sistema de Retail — Arquitectura de Microservicios

Este proyecto es un sistema de retail basado en microservicios, desarrollado como desafío personal, que simula la operación de una tienda:
gestión de productos, stock, carrito de compras, facturación y notificaciones.

El objetivo principal fue aplicar y consolidar conocimientos en arquitecturas distribuidas, mensajería asíncrona, autenticación centralizada y CI/CD automatizado.


⚙️ Arquitectura general

El sistema se compone de varios microservicios independientes, cada uno con su propia base de datos y responsabilidades específicas:

🛒 CarritoService – Manejo de carritos y productos agregados por los usuarios.

📦 ProductoService – Gestión de productos y stock.

💰 VentaService – Generación de facturas y cálculo de impuestos.

🔐 AuthService (Keycloak) – Autenticación y autorización centralizadas.

📨 NotificationService – Envío de notificaciones por mensajería asíncrona (RabbitMQ).

⚙️ ConfigServer – Configuración externa centralizada.

🌐 APIGateway – Puerta de entrada única para todos los microservicios.

🔎 Eureka Server – Registro y descubrimiento dinámico de servicios.


💡 Objetivos del proyecto

Desarrollar un sistema modular, escalable y desacoplado mediante microservicios.

Implementar comunicación asíncrona entre servicios con RabbitMQ.

Aplicar seguridad centralizada con Keycloak y roles definidos por microservicio.

Practicar integración de múltiples tecnologías en un mismo ecosistema.
