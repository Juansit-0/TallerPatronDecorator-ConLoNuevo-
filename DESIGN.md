# DESIGN.md

## Mundo visual

**Cafetería cálida de especialidad.** Superficies crema, tinta espresso, acentos caramelo
y ámbar, texturas suaves de papel. Cálido, artesanal y limpio; se siente como un menú de
cafetería de barrio moderno, no como un panel corporativo.

## Tipografía

- **Display:** `Fraunces` (serif cálida) para marca, títulos y totales.
- **Cuerpo:** `Inter` para etiquetas, controles y textos.
- Números tabulares en precios y totales.

## Paleta (tokens)

| Token | Valor | Uso |
|---|---|---|
| `--bg` | `#f4ece1` | Fondo cálido |
| `--surface` | `#fffdfa` | Paneles |
| `--espresso` | `#38231a` | Tinta principal / marca |
| `--caramel` | `#c17a34` | Acento / selección |
| `--caramel-deep` | `#9d5f21` | Totales y énfasis |
| `--latte` | `#d8b98c` | Swatches / leche |
| `--terracotta` | `#b4623a` | Estados de error |
| `--success` | `#6f7d4a` | Descuentos (Happy Hour) |

## Componentes

- **Drink card** (radio): swatch de color de la bebida, nombre y precio base.
- **Segmented control** para el tamaño.
- **Chip toggle** por extra, con precio; los descuentos van en verde.
- **Taza SVG** reactiva: el líquido cambia de color según la base, el tamaño escala y cada
  extra añade una capa visual (crema, caramelo, leche, hielo…).
- **Cadena de decoradores**: lista de capas de fuera hacia adentro con su aporte de precio.
- **Recibo**: desglose por capa y total, con separadores punteados.
- **Historial**: pedidos en memoria y recaudo acumulado.

## Movimiento

- Animaciones sutiles: entrada de capas, vapor en la taza (se oculta si la bebida es
  helada), hover de tarjetas y botones.
- Respeta `prefers-reduced-motion`.

## Accesibilidad

- Controles con roles/estado ARIA (`radiogroup`, `aria-checked`, `aria-pressed`).
- Foco visible con outline caramelo.
- Contraste de texto principal sobre crema.
