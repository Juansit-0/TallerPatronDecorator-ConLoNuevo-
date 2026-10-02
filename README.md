# Decorator Pattern Workshop — Coffee Shop Order System

Sistema de pedidos de una cafetería en línea creado para demostrar el patrón de diseño
**Decorator** implementado **desde cero**. No se usan decoradores de frameworks: la
jerarquía `Beverage` / `BeverageDecorator` y todos los `*Decorator` son código propio.

- **Código e identificadores en inglés.**
- **Interfaz de usuario en español**, cálida y gráfica (tema "cafetería").
- **Backend:** Java puro sobre el `com.sun.net.httpserver.HttpServer` del JDK.
- **Frontend:** HTML + CSS + JavaScript con una taza SVG que reacciona a las elecciones.
- **Sin** Spring, **sin** Maven/Gradle: solo `javac` y `java`.

## Documentación

| Documento | Contenido |
|---|---|
| [`docs/CASE_STUDY.md`](docs/CASE_STUDY.md) | El caso de estudio: problema, solución, roles del patrón, cálculo del costo y SOLID. |
| [`docs/IMPLEMENTATION_PLAN.md`](docs/IMPLEMENTATION_PLAN.md) | Infraestructura, módulos, estado de implementación y roadmap para el siguiente grupo. |

## Requisitos

- Java 17 o superior (probado con **Java 21 / Temurin**).
- `bash` (para los scripts). No se necesita ninguna dependencia externa.

## Cómo ejecutar

```bash
bash scripts/build.sh        # compila todo en out/
bash scripts/run-demo.sh     # demo de consola (imprime descripción y costo)
bash scripts/run-server.sh   # servidor web en http://localhost:8080
bash scripts/test.sh         # ejecuta la suite de pruebas
```

El servidor se debe lanzar desde la raíz del proyecto para que encuentre
`frontend/index.html`.

## Probar en general

```bash
bash scripts/run-server.sh
```

Luego abre **http://localhost:8080** en el navegador.

## Arquitectura

```
src/coffeeshop/
├── domain/          # Núcleo del patrón Decorator
│   ├── Beverage.java            # Component
│   ├── BaseBeverage.java        # ConcreteComponent (base abstracta)
│   ├── drinks/                  # Espresso, Americano, Latte, Tea
│   └── decorators/              # BeverageDecorator + decoradores concretos
├── application/     # Orquestación (casos de uso)
│   ├── OrderService.java        # Construye la cadena decorada
│   ├── BeverageCatalog.java     # Catálogo de bebidas y extras
│   ├── model/                   # OrderRequest, OrderResult, ReceiptLine...
│   └── enums/                   # DrinkType, Size, ExtraType
├── infra/           # Adaptadores de entrada/salida
│   ├── CoffeeShopServer.java    # HttpServer del JDK
│   ├── Json.java                # Serializador JSON propio
│   └── http/                    # Handlers de la API y de archivos estáticos
└── DemoMain.java
```

## El patrón Decorator en este proyecto

| Rol del patrón | Clase |
|---|---|
| **Component** | `Beverage` |
| **ConcreteComponent** | `Espresso`, `Americano`, `Latte`, `Tea` (vía `BaseBeverage`) |
| **Decorator** | `BeverageDecorator` |
| **ConcreteDecorator** | `ExtraShotDecorator`, `MilkDecorator`, `CaramelDecorator`, `VanillaDecorator`, `WhippedCreamDecorator`, `SizeDecorator`, y los extras propios (`HoneyDecorator`, `OatMilkDecorator`, `DecafDecorator`, `IcedDecorator`, `HappyHourDecorator`) |
| **Client** | `OrderService` |

Cada extra es un objeto que **envuelve** a otro `Beverage`, delega en él y añade su
propia descripción y costo. Las combinaciones se arman en tiempo de ejecución:

```java
Beverage order = new SizeDecorator(
                    new CaramelDecorator(
                        new ExtraShotDecorator(
                            new Latte())),
                    Size.LARGE);
```

Para `LATTE` + extra shot + caramelo + grande el costo se calcula recursivamente
hacia adentro y da **5.90**.

> Nota: `HappyHourDecorator` **resta** al costo, demostrando que un decorador no solo
> suma; es una prueba viva del principio Abierto/Cerrado.

## Módulos y estado

| # | Módulo | Estado |
|---|---|---|
| M0 | Infraestructura, scripts y documentación | **Implementado** |
| M1 | Dominio: `Beverage` + bebidas concretas | **Implementado** |
| M2 | Decoradores base y decoradores del enunciado | **Implementado** |
| M3 | Capa de aplicación (`OrderService`, catálogo, modelos) | **Implementado** |
| M4 | Demo de consola y pruebas (15 casos) | **Implementado** |
| M5 | Backend HTTP (JDK `HttpServer`) + JSON propio | **Implementado** |
| M6 | Frontend gráfico (taza SVG, visualizador de cadena, recibo, historial) | **Implementado** |
| M6x | Decoradores propios extra (miel, avena, descafeinado, helado, Happy Hour) | **Implementado** |
| M7 | Persistencia de pedidos (archivo/base de datos) | **Pendiente** |
| M8 | Extras de interfaz (i18n ES/EN, tema oscuro, exportar recibo) | **Pendiente** |
| M9 | Despliegue (empaquetado y publicación del servidor y el frontend) | **Pendiente** |

El detalle de lo implementado y lo pendiente está en
[`docs/IMPLEMENTATION_PLAN.md`](docs/IMPLEMENTATION_PLAN.md), con la guía de handoff
para el siguiente grupo.

## API REST

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/catalog` | Bebidas, tamaños y extras con sus precios. |
| `POST` | `/api/order` | Crea un pedido y devuelve descripción, total y desglose por capa. |
| `GET` | `/api/orders` | Historial de pedidos, número y recaudo acumulado. |

Ejemplo de petición:

```bash
curl -X POST http://localhost:8080/api/order \
  -H 'Content-Type: application/json' \
  -d '{"base":"LATTE","size":"LARGE","extras":["SHOT","CARAMEL"]}'
```

El frontend admite **deep-links** para compartir un pedido:

```
http://localhost:8080/?base=LATTE&size=LARGE&extras=SHOT,CARAMEL,WHIP
```

> Nota: `com.sun.net.httpserver` vive en el módulo `jdk.httpserver`, que no se resuelve
> por defecto en modo classpath. Los scripts lo añaden con `--add-modules jdk.httpserver`.
> Si ejecutas a mano: `java --add-modules jdk.httpserver -cp out coffeeshop.CoffeeShopServer`.
