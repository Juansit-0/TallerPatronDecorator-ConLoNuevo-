# Caso de estudio — Decorator Pattern Workshop: Coffee Shop Order System

## 1. El caso

Una cafetería en línea permite al cliente elegir una **bebida base**
(Espresso, Americano, Latte, Té), un **tamaño** y cualquier combinación de
**extras** (shot extra, leche, caramelo, vainilla, crema batida). El precio y la
descripción deben actualizarse para cada combinación posible.

## 2. El problema

Con herencia simple haría falta una subclase por cada combinación:
`LatteWithCaramelAndShot`, `LatteWithMilk`, `LatteWithMilkAndCaramel`, etc.
Con 4 bebidas, 5 extras y 3 tamaños son **4 × 2⁵ × 3 = 384 clases**, y cada nuevo
extra **duplica** el número otra vez. La herencia no escala para combinaciones
dinámicas.

## 3. La solución: Decorator

Los extras se convierten en objetos que **envuelven** a la bebida e implementan su
misma interfaz. Cada envoltorio delega en el objeto interno y añade su propia
descripción y costo. Las combinaciones se **arman en tiempo de ejecución**, no en
tiempo de compilación.

| Rol del patrón | Clase |
|---|---|
| Component | `Beverage` |
| ConcreteComponent | `Espresso`, `Americano`, `Latte`, `Tea` (vía `BaseBeverage`) |
| Decorator | `BeverageDecorator` |
| ConcreteDecorator | `ExtraShotDecorator`, `MilkDecorator`, `CaramelDecorator`, `VanillaDecorator`, `WhippedCreamDecorator`, `SizeDecorator` |
| Client | `OrderService` |

Todas las clases anteriores son **código propio del proyecto**: no se usa ningún
decorador aportado por un framework.

## 4. Cómo se calcula el costo

Para **Latte + shot extra + caramelo + grande** el objeto construido es:

```
SizeDecorator( CaramelDecorator( ExtraShotDecorator( Latte ) ) )
```

Al llamar `getCost()` en la capa externa, el cálculo **recursivo** avanza hacia
adentro y cada capa suma lo suyo al resultado de la anterior:

```
Size(LARGE)      -> +1.00
  Caramel        -> +0.60
    ExtraShot    -> +0.80
      Latte      ->  3.50
------------------------------
Total              = 5.90
```

El cliente no necesita saber cuántas capas existen: el polimorfismo lo resuelve.

## 5. Flujo de una petición

```
Navegador (frontend en español)
   → POST /api/order  { base: "LATTE", size: "LARGE", extras: ["SHOT","CARAMEL"] }
   → CoffeeShopServer
   → OrderService.build(...)  construye la cadena decorada
   → JSON con descripción, costo, desglose por capa y datos visuales
   → el frontend dibuja la taza y el recibo
```

## 6. OOP y SOLID

- **Abstracción:** `Beverage` oculta todos los tipos concretos.
- **Encapsulamiento:** los campos son privados o protegidos; el estado solo se lee
  por métodos.
- **Polimorfismo:** decoradores y bebidas son intercambiables.
- **Composición sobre herencia:** los extras se envuelven, no se heredan.
- **Abierto/Cerrado:** se agrega un extra nuevo sin tocar las clases existentes.
- **Responsabilidad única:** cada decorador añade exactamente una cosa.

## 7. Extenderlo (Abierto/Cerrado)

Para agregar un extra nuevo, por ejemplo miel:

1. Crear `HoneyDecorator extends BeverageDecorator` con su etiqueta y precio.
2. Registrarlo en el catálogo (`BeverageCatalog`).
3. Añadir su entrada en el catálogo que consume el frontend.

No se modifica ningún decorador ni bebida existente. El proyecto incluye varios
extras propios además del enunciado, entre ellos `HappyHourDecorator`, que
**resta** al costo y demuestra que un decorador no tiene por qué sumar.

## 8. Límites del patrón

- Aparecen muchas clases pequeñas.
- Depurar cadenas muy largas es más difícil.
- El orden de envoltura puede importar si los extras interactúan entre sí.
