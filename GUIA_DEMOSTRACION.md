# Guía de demostración — Software para Restaurante

Guion para presentar el **flujo básico** del sistema en **5–7 minutos**: una comanda que nace en
el salón, se reparte entre cocina y bar, vuelve al salonero y termina cobrada en caja.

> Todo lo que **no** cabe en el tiempo está listado al final, en *"Si preguntan"*, con la ruta
> exacta para llegar. No hay que memorizarlo: solo saber dónde está.

---

## Antes de empezar (con tiempo, no frente a la profe)

1. **Encender MySQL** y confirmar que existe `restaurante_db`.

2. **Correr el script de la base**, para arrancar siempre con los mismos datos:
   ```
   mysql -u root -p < ScriptPROG4.sql
   ```
   O en Workbench: `File > Open SQL Script` y darle a **Execute all** (el rayo).

   > Ojo: el script arranca borrando la base y volviéndola a crear, así que se puede correr
   > las veces que sea. No hay que crear la base a mano ni escoger el esquema antes.
   > `restaurante_db_dump.sql` es el mismo contenido en formato de respaldo de MySQL, por si
   > se prefiere restaurar así.

3. **Generar la rotación del día.** Entrar como `admin` / `admin123` (radio **Administrador**),
   pestaña **Rotación** → la fecha de hoy ya viene puesta → **Guardar** → responder **Sí** a
   *"Ese día no tiene rotación. ¿Generarla automáticamente?"*.

   **Anotar qué sección le tocó a SAL001 (Ana Solís).** Cerrar sesión.

   > ⚠️ **Sin este paso el salonero abre su pantalla sin mesas** y la demo se cae. Hacerlo antes
   > ahorra ~1 minuto en vivo.

4. **Abrir el proyecto en NetBeans** (carpeta `ProyectoFinal`), correrlo una vez para confirmar
   que compila y aparece el login, y **cerrarlo**.

### Usuarios que se usan en el guion

| Rol | Usuario | Contraseña | Radio |
|---|---|---|---|
| Salonero | `SAL001` (Ana Solís) | `pass123` | Empleado |
| Cocinero | `COS001` (Pedro Rojas) | `pass123` | Empleado |
| Bartender | `BAR001` (Diego Campos) | `pass123` | Empleado |
| Cajero | `CAJ001` (María López) | `pass123` | Empleado |
| Administrador | `admin` | `admin123` | Administrador |

---

## El guion (5–7 min)

Cronómetro aproximado a la izquierda. **No devolverse.**

### `0:00` — PASO 1 · Salonero: tomar la orden

**Entrar:** `SAL001` / `pass123`, radio **Empleado**.

Arriba se lee **"Salonero: Ana Solís"** y **"Sección asignada hoy: …"**

> **Decir:** *"Las contraseñas se guardan cifradas con MD5, nunca en texto plano. Según el rol, el
> login abre una ventana distinta. Además el sistema ya le dice al salonero en qué sección le toca
> hoy, porque los saloneros rotan de sección todos los días."*

Pestaña **Mesas y Comandas**:

1. En el combo de arriba, escoger una mesa que diga **(libre)**.
2. **Plato:** categoría `Plato Fuerte` → platillo `Filete de Res   ₡12000.00` → cantidad `2` →
   **Agregar Plato**.
3. **Bebida:** categoría `Licor` → `Cerveza Nacional   ₡2500.00` → cantidad `3` →
   **Agregar Bebida**.
4. → **Generar Comanda**

Sale *"Comanda #NN generada para la mesa X"*. **Anotar ese número**, se usa en todos los pasos
siguientes.

> **Decir:** *"El menú se escoge por categoría y platillo, con el precio a la vista; el salonero no
> tiene que saberse códigos. Solo aparece lo que está activo hoy. Al generar la comanda se guarda
> la hora, porque hay un tope de 20 minutos para servirla, la mesa queda ocupada, y el pedido se
> reparte solo: los platos a cocina y las bebidas al bar."*

**Cerrar sesión.**

---

### `1:45` — PASO 2 · Cocina

**Entrar:** `COS001` / `pass123`.

1. Seleccionar la comanda anotada → **Detalle Comanda** → **aparece solo el Filete de Res**.
2. Con la comanda seleccionada → **Proceso Cocina**.

Sale: *"Platos listos. Falta que el bar termine las bebidas."*

> **Decir:** *"La cocina ve únicamente los platos, las bebidas no son de su área. Y fíjense en la
> coordinación: la comanda no pasa a 'lista' porque el bar todavía no entrega. El salonero no debe
> ir a servir medio pedido."*

**Cerrar sesión.**

---

### `2:30` — PASO 3 · Bar

**Entrar:** `BAR001` / `pass123`.

1. Seleccionar la misma comanda → **Detalle Bar** → **aparece solo la Cerveza Nacional**.
2. → **Marcar Lista Bar**.

Ahora sí: *"Comanda #NN lista para servir."*

> **Decir:** *"Como la cocina ya había terminado, al cerrar el bar la comanda pasa a 'lista'.
> Recién ahí el salonero puede ir a servir."*

**Cerrar sesión.**

---

### `3:15` — PASO 4 · Salonero: servir y cerrar

**Entrar otra vez:** `SAL001` / `pass123` → pestaña **Mis Comandas**.

En el panel de abajo está la **notificación**:

```
PEDIDO LISTO | Mesa 7 | comanda #10 | listo a las 20:08
     Platillos: 2 Filete de Res · Bebidas: 3 Cerveza Nacional
```

> **Decir:** *"El salonero recibe el aviso con la mesa, la hora en que quedó listo y qué platillos y
> bebidas puede ir a servir. En rojo aparecen las comandas que pasaron los 20 minutos."*

Seleccionar la comanda (ya en estado `lista`) → **Cerrar Comanda** → confirmar.

Sale la **factura provisional**:

```
FACTURA PROVISIONAL #10
Comanda #10   Mesa 7
Salonero: Ana Solís

2 x Filete de Res    ₡24000.00
3 x Cerveza Nacional ₡7500.00

Total antes de impuesto: ₡31500.00
IVA (13%): ₡4095.00
Total a pagar: ₡35595.00

Pasa a caja para ser cancelada.
```

La mesa **queda libre** otra vez.

> **Decir:** *"Al cerrar la comanda sale la factura provisional, que es la que ve el cliente, ya con
> el IVA del 13 %. Esa misma factura es la que pasa a caja."*

**Cerrar sesión.**

---

### `4:30` — PASO 5 · Caja: cobrar

**Entrar:** `CAJ001` / `pass123`.

1. Seleccionar la comanda → **Detalle Caja**. Se cargan las líneas y abajo:
   ```
   SubTotal: ₡31 500  |  IVA (13%): ₡4 095  |  Total: ₡35 595
   ```
2. → **Generar Factura** → confirmar.

La comanda **desaparece de la lista** (ya está cobrada).

3. → **Historial Facturas**: la factura aparece como **`final / pagada`**, con **el mismo número**
   de la provisional.

> **Decir:** *"El impuesto de ventas es del 13 % sobre el subtotal; el precio del menú va sin
> impuesto. Y no se duplica la factura: la provisional que sacó el salonero es la que se cancela
> aquí, cambia de provisional a final y queda pagada con el código del cajero que la cobró."*

**Cerrar sesión.**

---

### `5:45` — Cierre

**Entrar:** `admin` / `admin123` → pestaña **Reportes** →
**"Comandas atendidas en Cocina (Salón vs Bar)"** → **Generar**.

> **Decir:** *"Los reportes se hacen con JFreeChart dentro de la misma ventana. En este aparece la
> comanda que acabamos de crear en vivo."*

---

## Resumen del recorrido

```
SAL001  → comanda (2 Filete de Res + 3 Cerveza)  → sale a cocina y bar
COS001  → despacha los platos                    → queda 'en proceso'
BAR001  → despacha las bebidas                   → queda 'lista'
SAL001  → ve la notificación y cierra            → factura PROVISIONAL (IVA 13%)
CAJ001  → cobra: la misma factura pasa a final/pagada
admin   → reporte con la comanda de la demo
```

---

## Si preguntan (fuera del guion)

Nada de esto está en los 6 minutos, pero está hecho y se llega en un clic:

| Si preguntan por… | Dónde está |
|---|---|
| Login que rechaza contraseñas malas | Login: `SAL001` con cualquier clave → *"Usuario o contraseña incorrectos"*, la ventana no se cierra |
| Reservas y disponibilidad | Salonero → pestaña **Reservas**: **Disponibilidad** dice cuántas mesas necesita el grupo, cuáles están libres y, si no hay, **a qué hora se desocupa una**. Guardar deja la reserva **con mesa asignada** |
| Código de empleado (`SAL001`) | Admin → **Usuarios**: escoger Tipo `Cocinero` y cédula `2-0456-0789` → el código se arma solo: **`COS789`** (prefijo del rol + últimos 3 dígitos) |
| Rotación diaria de secciones | Admin → **Rotación**: cada salonero pasa a la sección siguiente; la base impide dos secciones el mismo día |
| CRUD / mantenimientos | Admin → **Comidas**, **Bebidas**, **Categorías**, **Secciones**, **Mesas**, **Usuarios**, **Tipos** |
| Validaciones antes de borrar | Admin → **Mesas**: borrar una **Ocupada** avisa; **Secciones**: borrar una con mesas dice cuántas tiene |
| Producto inactivo | La **Malteada Vainilla** está inactiva a propósito: **no aparece** en la lista del salonero |
| Cuenta separada | Caja → **Detalle Caja** → marcar **"Cuenta Separada?"** → seleccionar líneas → **Dividir Cuenta**. La comanda solo se cierra cuando no queda nada pendiente |
| Comanda propia del bar | Bar → **Crear Comanda**: pregunta ítem por ítem, escogiendo del desplegable (nombre, categoría y precio, sin códigos). No ocupa mesa, y si el cliente pide comida **esa parte se manda a la cocina** |
| Los otros 4 reportes | Admin → **Reportes**: personas por día (salón y bar), comandas realizadas y atendidas en el bar |

---

## Si algo sale mal

| Problema | Solución |
|---|---|
| El salonero no tiene mesas | Falta generar la **rotación del día** como admin (paso 3 de la preparación) |
| "No se pudo conectar a la base de datos" | MySQL apagado, o la contraseña de `ConnectionDB.java` no coincide |
| La comanda no aparece en cocina | Solo llega a cocina si tiene **platos**. Si solo tenía bebidas, buscarla en el bar |
| La comanda no aparece en caja | La caja solo muestra comandas `lista` o `cerrada`. Falta que cocina y bar la despachen |
| Todas las comandas salen "atrasadas" | Normal: las de la base de prueba tienen horas viejas. Sirve para mostrar la regla de los 20 minutos |
| No compila en otra máquina | Las librerías están en `ProyectoFinal/lib/` y vienen con el repositorio. La plataforma Java del proyecto debe ser **JDK 25** |
