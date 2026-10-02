# PRODUCT.md

## Producto

**Cafetería Decorator** — sistema de pedidos en línea de una cafetería que sirve como
demonstración viva del patrón de diseño **Decorator**. El cliente elige una bebida base,
un tamaño y cualquier combinación de extras; el precio y la descripción se recalculan
para cada combinación.

## Objetivo

Que un estudiante o evaluador pueda **ver el patrón en acción**: cada extra envuelve a la
bebida anterior y el resultado (descripción, costo, cadena de capas) se muestra en vivo.

## Usuarios

- **Cliente de cafetería** (público general): quiere armar su bebida y ver el precio.
- **Estudiante / evaluador del taller**: quiere entender el patrón y probar variaciones
  (incluido un decorador con costo negativo).

## Superficie

- **Modo: Operate.** Es una herramienta donde el usuario completa una tarea (armar un
  pedido). La marca vive en los detalles precisos, no en la expresión gratuita.

## Plataforma

Web de escritorio y móvil, servida por el propio proceso Java en `http://localhost:8080`.

## Restricciones

- Backend Java puro (JDK `HttpServer`), sin frameworks de aplicación ni librerías.
- Código e identificadores en inglés; interfaz de usuario en español.
- Los decoradores se implementan a mano, nunca desde un framework.
- Sin `npm`/bundlers: HTML, CSS y JavaScript servidos como archivos estáticos.

## Fuera de alcance

- Pagos reales, cuentas de usuario, bases de datos (el historial vive en memoria).

## Supuestos

_(Inferidos del brief del taller; se pueden corregir.)_

- El precio se muestra en dólares con dos decimales.
- El "Happy Hour" es un decorador de ejemplo con costo negativo fijo.
- El idioma principal de la interfaz es español.
