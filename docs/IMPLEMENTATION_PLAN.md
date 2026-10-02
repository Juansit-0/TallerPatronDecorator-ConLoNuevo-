# Plan de implementación

Documento de infraestructura, módulos y estado del proyecto. Sirve como guía para
completar el repositorio y como punto de partida para el siguiente grupo.

## 1. Restricciones y decisiones

| Tema | Decisión |
|---|---|
| Lenguaje | Java 17+ (probado con Java 21). |
| Frameworks de aplicación | Ninguno. Solo el JDK. |
| Build | Scripts `bash` + `javac` (sin Maven ni Gradle). |
| Pruebas | Runner propio (cero dependencias). |
| Backend web | `com.sun.net.httpserver.HttpServer` del JDK. |
| Serialización | `Json` escrito a mano (sin librerías). |
| Frontend | HTML/CSS/JS estático servido por el backend. |
| Decoradores | **Implementados por el equipo**, jamás de framework. |
| Idioma | Código e identificadores en inglés; interfaz en español. |

## 2. Infraestructura del repositorio

```
decoratorrrr/
├── README.md
├── docs/
│   ├── CASE_STUDY.md
│   └── IMPLEMENTATION_PLAN.md
├── scripts/            # build.sh, run-demo.sh, run-server.sh, test.sh
├── src/coffeeshop/     # código de producción
├── test/coffeeshop/    # pruebas
└── frontend/           # index.html, styles.css, app.js, assets/
```

Convenciones:

- Paquete raíz `coffeeshop`, subpaquetes `domain`, `application`, `infra`.
- Un tipo público por archivo.
- Autoría de commits con correos `noreply` de GitHub, sin `Co-authored-by`.
- Flujo git: una rama por integrante (`jeni`, `drako`, `miguel`) integrada a `main`
  mediante *pull requests* aprobados sin comentarios.

## 3. Módulos

| # | Módulo | Estado |
|---|---|---|
| M0 | Infraestructura y documentación | Implementado |
| M1 | Dominio: Component y bebidas | Implementado |
| M2 | Decoradores del enunciado | Implementado |
| M3 | Capa de aplicación | Implementado |
| M4 | Demo y pruebas | Implementado |
| M5 | Backend HTTP | Implementado |
| M6 | Frontend gráfico | Implementado |
| M6x | Decoradores propios extra | Implementado |
| M7 | Persistencia | Pendiente |
| M8 | Extras de interfaz | Pendiente |

### M0 — Infraestructura y documentación
`.gitignore`, estructura de carpetas, scripts de build/ejecución/test, `README.md`,
`CASE_STUDY.md` y este plan.

**Estado: implementado.**

### M1 — Dominio: Component y bebidas
- `Beverage` (interface): `getDescription()`, `getCost()`, `getIngredients()`.
- `BaseBeverage` (abstract): estado `description` y `cost`.
- `Espresso`, `Americano`, `Latte`, `Tea`.

### M2 — Decoradores
- `BeverageDecorator` (abstract): guarda un `Beverage` y delega.
- Decoradores concretos del enunciado: `ExtraShotDecorator`, `MilkDecorator`,
  `CaramelDecorator`, `VanillaDecorator`, `WhippedCreamDecorator`, `SizeDecorator`.
- Decoradores propios que refuerzan el patrón: `HoneyDecorator`, `OatMilkDecorator`,
  `DecafDecorator`, `IcedDecorator`, `HappyHourDecorator` (costo negativo).

### M3 — Capa de aplicación
- Enums `DrinkType`, `Size`, `ExtraType`.
- Modelos `OrderRequest`, `OrderResult`, `ReceiptLine`, `CatalogItem`.
- `BeverageCatalog`: fábrica de bebidas y decoradores.
- `OrderService`: construye la cadena decorada y arma el resultado.

### M4 — Demo y pruebas
- `DemoMain`: pedidos de ejemplo por consola.
- `coffeeshop.tests.TestRunner`: micro-framework de aserciones y suite de pruebas
  (costo recursivo, descripción, combinaciones, decorador con costo negativo).

### M5 — Backend HTTP
- `CoffeeShopServer` + handlers: `GET /api/catalog`, `POST /api/order`,
  `GET /api/orders` (historial), estáticos del `frontend/`.
- `Json`: serializador/parser mínimo.

### M6 — Frontend gráfico
- `index.html`, `styles.css`, `app.js`, `assets/cup.svg`.
- Taza SVG reactiva (color por bebida, escala por tamaño, capas por decorador).
- Visualizador de la cadena decoradora anidada.
- Recibo con desglose por capa y total en vivo.
- Tema visual "cafetería" (crema, espresso, caramelo, ámbar), interfaz en español.

## 4. Roadmap para el siguiente grupo (pendientes)

Lo que **falta** implementar. El resto del sistema ya está funcional y es la base sobre la
que continuar.

| # | Pendiente | Notas |
|---|---|---|
| M7 | **Persistencia de pedidos** | El historial vive en memoria (`/api/orders`), por lo que se pierde al reiniciar. Falta guardarlo en archivo o base de datos, con carga al arrancar, paginación y totales por fecha. |
| M8 | **Extras de interfaz** | Conmutador ES/EN, tema claro/oscuro, exportar/imprimir el recibo, accesibilidad avanzada y estados vacíos más ricos. |
| M9 | **Decoradores adicionales** | P. ej. `LoyaltyDecorator` (descuento por cliente frecuente), `SeasonalFlavorDecorator` o combos, siempre creados a mano. |
| M10 | **Constructor de decoradores en la UI** | Permitir al usuario combinar y **reordenar** capas y ver cómo cambia la cadena y el costo (el orden puede importar). |

### Cómo continuar

1. Partir de `main` y crear una rama por integrante (como en este proyecto).
2. Respetar las restricciones: código en inglés, interfaz en español, decoradores propios
   (sin decoradores de frameworks).
3. Actualizar la tabla de estado de este documento y la del `README.md` al cerrar cada módulo.
4. Verificar con `scripts/test.sh` y `scripts/build.sh` antes de integrar.

## 5. Cómo compilar y verificar

```bash
bash scripts/build.sh
bash scripts/test.sh
bash scripts/run-demo.sh
bash scripts/run-server.sh
```
