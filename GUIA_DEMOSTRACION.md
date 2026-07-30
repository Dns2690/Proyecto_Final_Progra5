# Guía de demostración — Software para Restaurante

Guion paso a paso para presentar el sistema. Está armado para recorrer **todos los
requerimientos del enunciado** en orden, sin devolverse.

**Duración estimada:** 15–20 minutos.

---

## Antes de empezar (hacerlo con tiempo, no frente a la profe)

1. **Encender MySQL** y confirmar que la base `restaurante_db` existe.

2. **Restaurar el respaldo** para arrancar con los datos de demostración:
   ```
   mysql -u root -p < restaurante_db_dump.sql
   ```
   > Ojo: esto borra la base actual y la vuelve a crear.

3. **Abrir el proyecto en NetBeans** (carpeta `ProyectoFinal`) y correrlo una vez para
   confirmar que compila y que aparece la ventana de login.

4. **Cerrar la aplicación** y dejarla lista para arrancar de cero en la exposición.

### Usuarios del sistema

| Rol | Usuario | Contraseña | Radio a marcar |
|---|---|---|---|
| Administrador | `admin` | `admin123` | Administrador |
| Salonero | `SAL001` (Ana Solís) | `pass123` | Empleado |
| Salonero | `SAL002` (Luis Mora) | `pass123` | Empleado |
| Salonero | `SAL003` (Carmen Vega) | `pass123` | Empleado |
| Salonero | `SAL567` (Sofía Herrera) | `nueva456` | Empleado |
| Cocinero | `COS001` (Pedro Rojas) | `pass123` | Empleado |
| Bartender | `BAR001` (Diego Campos) | `pass123` | Empleado |
| Cajero | `CAJ001` (María López) | `pass123` | Empleado |

### Códigos del menú (para digitar durante la demo)

**Comidas**

| Código | Plato | Categoría | Precio |
|---|---|---|---|
| 1 | Ensalada César | Ensalada | ₡4 500 |
| 2 | Casado de Pollo | Plato Fuerte | ₡7 500 |
| 3 | Filete de Res | Plato Fuerte | ₡12 000 |
| 4 | Hamburguesa BBQ | Comida Rápida | ₡8 500 |
| 5 | Alitas Buffalo | Comida Rápida | ₡9 000 |
| 6 | Flan Casero | Postre | ₡2 500 |
| 7 | Ceviche de Corvina | Mariscos | ₡7 200 |

**Bebidas**

| Código | Bebida | Categoría | Precio | |
|---|---|---|---|---|
| 1 | Agua Natural 500ml | Bebida Fría | ₡1 000 | |
| 2 | Refresco Natural | Bebida Fría | ₡1 500 | |
| 3 | Café Americano | Bebida Caliente | ₡1 800 | |
| 4 | Malteada Vainilla | Malteada | ₡3 500 | **inactiva** |
| 5 | Cerveza Nacional | Licor | ₡2 500 | |
| 6 | Chocolate Caliente | Bebida Caliente | ₡2 200 | |

> La **Malteada Vainilla (código 4)** está inactiva a propósito. Sirve para demostrar que el
> sistema no deja agregar a una comanda algo que no está disponible.

---

## PASO 0 — Arrancar

Correr el proyecto. Abre la ventana **"Restaurante - Login"**.

> **Decir:** *"La aplicación arranca verificando la conexión con la base de datos; si no
> conecta, avisa y no abre nada."*

---

## PASO 1 — Seguridad del login

**Digitar:** usuario `SAL001`, contraseña `loquesea` → **Ingresar**

Aparece en rojo: *"Usuario o contraseña incorrectos"* y la ventana **no** se cierra.

> **Decir:** *"Las contraseñas se guardan cifradas con MD5. El sistema no compara textos, cifra
> lo que se digitó y compara los hashes. En la base nunca está la contraseña real."*

*(Si quiere mostrarlo: en la tabla `usuario` la contraseña se ve como
`bb1dfaf399e9ac67760b674f47ef549d`.)*

---

## PASO 2 — Administrador: preparar el día

**Entrar:** `admin` / `admin123`, marcando **Administrador**.

> **Decir:** *"Según el rol, el login abre una ventana distinta. Un salonero nunca puede llegar
> a esta pantalla."*

### 2.1 Rotación del día ← **HACER ESTO PRIMERO, es indispensable**

Pestaña **Rotación**:

1. En **Fecha** debe estar la fecha de hoy (`aaaa-mm-dd`).
2. Clic en **Guardar**.
3. Pregunta: *"Ese día no tiene rotación. ¿Generarla automáticamente?"* → **Sí**.
4. Avisa cuántos saloneros se asignaron y la tabla se llena.

> **Decir:** *"Cada día los saloneros rotan de sección. El sistema arma la rotación
> automáticamente: cada uno pasa a la sección siguiente a la que tuvo la última vez. La base
> tiene restricciones que impiden que un salonero tenga dos secciones el mismo día o que una
> sección la atiendan dos saloneros."*

> ⚠️ **Sin este paso, el salonero abre su pantalla sin mesas.** No saltárselo.

**Anotar qué sección le tocó a SAL001 (Ana Solís)** — se necesita en el paso 3.

### 2.2 CRUD de comidas

Pestaña **Comidas**:

1. Clic en cualquier fila → el formulario se llena solo.
2. Crear uno nuevo: nombre `Sopa Negra`, categoría `Plato Fuerte`, precio `3800`,
   descripción `Con huevo duro` → **Guardar**.
3. Mostrar un error a propósito: escribir precio `abc` → **Guardar** → avisa que debe ser
   número mayor a cero.

> **Decir:** *"Todos los mantenimientos funcionan igual: la tabla lista, el clic carga el
> formulario, y Guardar / Actualizar / Eliminar / Limpiar operan sobre ese registro."*

### 2.3 CRUD de bebidas y categorías

Enseñar rápido las pestañas **Bebidas** y **Categorías** (comida y bebida en la misma pantalla).

### 2.4 CRUD del salón

Pestaña **Secciones** → hay 4 secciones.
Pestaña **Mesas** → 20 mesas con su sección y disponibilidad.

**Demostrar dos validaciones:**
- Seleccionar una mesa que diga **"Ocupada"** → **Eliminar** → avisa que está ocupada.
- En **Secciones**, escoger una que tenga mesas → **Eliminar** → avisa cuántas mesas tiene.

> **Decir:** *"Antes de borrar se revisa que el dato no esté en uso. Preferimos explicar el
> motivo en lugar de mostrar un error de llave foránea."*

### 2.5 Registro de usuarios ← **el código de empleado**

Pestaña **Usuarios**:

1. Escoger **Tipo:** `Cocinero`.
2. Escribir **Cédula:** `2-0456-0789`.
3. **Mirar el campo Código**: se llena solo → **`COS789`**.
4. Nombre: `Marta Ugalde`, Contraseña: `clave123`, Activo marcado → **Guardar**.

> **Decir:** *"El código se arma con el prefijo del rol y los últimos tres dígitos de la cédula,
> como pide el enunciado. Se genera solo mientras se escribe y no se puede editar, porque es la
> llave primaria de la tabla."*

5. Enseñar el botón **Contraseña**: selecciona un empleado y le cambia solo la clave.

### 2.6 Tipos de usuario

Pestaña **Tipos** → los 4 roles con su prefijo. Intentar guardar un prefijo de 2 letras → avisa
que deben ser exactamente 3.

**Cerrar sesión.**

---

## PASO 3 — Salonero: tomar la orden

**Entrar:** `SAL001` / `pass123` (Empleado).

Arriba se lee: **"Salonero: Ana Solís"** y **"Sección asignada hoy: …"**

> **Decir:** *"Al ingresar, el sistema le indica en qué sección trabaja hoy. El combo de mesas
> trae únicamente las mesas de esa sección, marcando cuáles están libres y cuáles ocupadas."*

### 3.1 Reservas y vista preliminar de disponibilidad

Pestaña **Reservas**:

1. **Vista preliminar.** Poner **Personas: 5**, hora `20:00`, y presionar **Disponibilidad**.
   Responde en verde con tres datos:
   ```
   Sí hay campo a las 20:00
   5 personas ocupan 2 mesa(s)
   Mesas libres: 1, 2, 3, 4, 6, 7, 8, 10 y 10 más
   ```

   > **Decir:** *"El sistema muestra qué mesas están libres a esa hora y cuántas necesita el grupo.
   > Se asume que en cada mesa caben 4 personas y que una reserva ocupa la mesa por 2 horas."*

2. **Cuando no hay campo** (opcional, si quiere mostrarlo): subir Personas a `20` en una hora ya
   ocupada → responde en rojo diciendo cuántas mesas faltan **y a qué hora se desocupa una**.

3. **Guardar una reserva:**
   - Nombre: `Familia Rodríguez`
   - Teléfono: `8888-9999`
   - Fecha: dejar la de hoy
   - Hora: `20:00`
   - Personas: `5`
   - Marcar **Incluye niños**
   - → **Guardar Reserva**

   Confirma con *"Reserva guardada en la mesa N"* — **la reserva queda con mesa asignada**.

4. **Cancelar:** seleccionar una reserva de la tabla → **Cancelar Reserva** → confirmar → queda
   en estado `cancelada`.

### 3.2 Armar la comanda ← **el corazón de la demo**

Pestaña **Mesas y Comandas**:

1. Escoger una mesa que diga **(libre)**.
2. **Código Plato:** escribir `3` y **presionar Enter** → aparece *"Filete de Res"*.
3. Cantidad: `2` → **Agregar Plato**.
4. **Código Bebida:** escribir `5` y Enter → *"Cerveza Nacional"*. Cantidad: `3` →
   **Agregar Bebida**.
5. *(Opcional)* Probar la bebida `4` → avisa que no está disponible.
6. *(Opcional)* **Quitar Ítem** con una línea seleccionada.
7. → **Generar Comanda**

Sale: *"Comanda #NN generada para la mesa X"*. **Anotar ese número**, se usa en los pasos 4, 5 y 6.

> **Decir:** *"Al generar la comanda se guarda la hora, porque hay un tope de 20 minutos para
> servirla. El pedido se reparte automáticamente: los platos van a cocina y las bebidas al bar.
> La mesa queda ocupada."*

### 3.3 Mis comandas y el panel de notificación

Pestaña **Mis Comandas** → aparece la comanda recién creada en estado `abierta`.

En el panel de abajo está la **notificación de pedidos listos**, con los tres datos que pide el
enunciado por cada comanda ya despachada:

```
PEDIDO LISTO | Mesa 7 | comanda #10 | listo a las 20:08
     Platillos: 2 Casado de Pollo · Bebidas: 2 Cerveza Nacional
```

Y al final, en rojo, cuántas comandas pasaron los 20 minutos.

> **Decir:** *"Cuando cocina y bar terminan, el salonero recibe la notificación con el número de
> mesa, la hora en que quedó listo y qué platillos y bebidas puede ir a servir. El aviso rojo es el
> control del tope de 20 minutos."*

### 3.4 Cerrar comanda → factura provisional

Seleccionar una comanda que diga **`lista`** → **Cerrar Comanda** → confirmar.

Sale la **factura provisional** con el detalle completo:

```
FACTURA PROVISIONAL #10
Comanda #10   Mesa 7
Salonero: Ana Solís

2 x Casado de Pollo   ₡15000.00
2 x Cerveza Nacional  ₡5000.00

Total antes de impuesto: ₡20000.00
IVA (13%): ₡2600.00
Total a pagar: ₡22600.00

Pasa a caja para ser cancelada.
```

La mesa queda libre. **Anotar el número de esa factura**, se usa en el paso 6.

> **Decir:** *"Al cerrar la comanda el salonero genera la factura provisional, que es la que ve el
> cliente. Esa misma factura pasa a caja para ser cancelada; no se crea otra."*

**Cerrar sesión.**

---

## PASO 4 — Cocina

**Entrar:** `COS001` / `pass123`.

1. Buscar la comanda anotada en la lista.
2. Seleccionarla → **Detalle Comanda** → **aparece solo el Filete de Res, no la cerveza**.

> **Decir:** *"La cocina ve únicamente los platos. Las bebidas no le aparecen porque no son de
> su área."*

3. Con la comanda seleccionada → **Proceso Cocina**.

Sale: *"Platos listos. Falta que el bar termine las bebidas."*

> **Decir:** *"Aquí está la coordinación entre áreas: la comanda no pasa a 'lista' porque el bar
> todavía no ha entregado. Queda 'en proceso'. El salonero no debe ir a servir medio pedido."*

**Cerrar sesión.**

---

## PASO 5 — Bar

**Entrar:** `BAR001` / `pass123`.

1. Buscar la misma comanda → **Detalle Bar** → **aparece solo la Cerveza Nacional**.
2. Seleccionarla → **Marcar Lista Bar**.

Ahora sí sale: *"Comanda #NN lista para servir."*

> **Decir:** *"Como la cocina ya había terminado, al cerrar el bar la comanda pasa a 'lista'.
> Recién ahí el salonero puede servir."*

### 5.1 Comanda propia del bar (con comida incluida)

Botón **Crear Comanda**. Va preguntando ítem por ítem:

1. *"¿Qué va a agregar?"* → **Bebida** → código `3` (Café Americano) → cantidad `2`
2. *"¿Agregar algo más?"* → **Sí**
3. *"¿Qué va a agregar?"* → **Comida** → código `2` (Casado de Pollo) → cantidad `1`
4. *"¿Agregar algo más?"* → **No**

Confirma: *"Comanda #NN creada con 2 ítem(s). **La comida se mandó a la cocina.**"*

> **Decir:** *"El bar atiende clientes que consumen solo en la barra, y esa comanda no ocupa mesa.
> Si el cliente del bar pide algo de comer, esa parte se remite a la cocina igual que una comanda
> de salón: el bar se queda con las bebidas y la cocina recibe los platos."*

*(Se puede comprobar entrando como `COS001`: la comanda del bar aparece en la cola de cocina.)*

**Cerrar sesión.**

---

## PASO 6 — Caja: cobrar con IVA

**Entrar:** `CAJ001` / `pass123`.

### 6.1 Cuenta junta

1. Buscar la comanda del paso 3 en la lista.
2. Seleccionarla → **Detalle Caja**.
3. Se cargan las líneas y abajo aparecen:
   ```
   SubTotal: ₡31 500  |  IVA (13%): ₡4 095  |  Total: ₡35 595
   ```

> **Decir:** *"El impuesto de ventas de Costa Rica es del 13 % y se aplica sobre el subtotal.
> El precio del menú está sin impuesto."*

4. **Generar Factura** → confirmar → sale el número de factura con el desglose.

Al volver, la comanda **desapareció de la lista** (ya está cobrada), quedó `cerrada` y **la mesa
se liberó**.

> **Si cobró la comanda que cerró en el paso 3.4:** el número de factura es **el mismo** de la
> provisional. Se puede mostrar en **Historial Facturas**, donde ahora aparece como
> `final / pagada` en vez de `provisional / pendiente`.
>
> **Decir:** *"La factura provisional que sacó el salonero es la que se cancela aquí. No se
> duplica: la misma factura cambia de provisional a final y queda pagada, con el código del
> cajero que la cobró."*

### 6.2 Cuenta separada ← **demostrar aparte**

1. Seleccionar otra comanda de la lista (queda alguna de la base de demostración).
2. **Detalle Caja** → se cargan sus líneas.
3. Marcar la casilla **"Cuenta Separada?"**.
4. Seleccionar **una o dos líneas** de la tabla de detalle (con clic, o Ctrl+clic para varias).
5. → **Dividir Cuenta** → confirmar.
6. Volver a seleccionar la misma comanda → **Detalle Caja** → **ahora solo aparece lo que
   falta**.
7. Seleccionar lo restante → **Dividir Cuenta** otra vez → ahí sí la comanda se cierra.

> **Decir:** *"La tabla `detalle_factura` permite repartir las líneas de una misma comanda entre
> varias facturas. El sistema lleva el control de lo ya cobrado, y la comanda solo se cierra
> cuando no queda nada pendiente."*

### 6.3 Historial

**Historial Facturas** → lista todas con el total facturado.

**Cerrar sesión.**

---

## PASO 7 — Reportes

**Entrar:** `admin` / `admin123` → pestaña **Reportes**.

Generar los cinco, uno por uno:

| # | Reporte | Qué señalar |
|---|---|---|
| 1 | Personas atendidas por día - Salón | Cruza comandas de salón con las reservas por mesa y fecha |
| 2 | Personas atendidas por día - Bar | Solo tiene datos cuando el bar atiende una mesa con reserva |
| 3 | Comandas realizadas en el Bar | Todas las de origen bar, por día |
| 4 | Comandas atendidas en el Bar | Las que el bar despachó |
| 5 | Comandas atendidas en Cocina (Salón vs Bar) | **Muestra la comanda que acabamos de crear** |

> **Decir:** *"Los reportes se generan con JFreeChart como gráficos de barras dentro de la misma
> ventana. Las consultas están agrupadas en una clase aparte de la capa de datos."*

**El reporte 5 es el mejor cierre**, porque incluye la comanda creada en vivo durante la demo.

---

## Resumen del recorrido

```
admin    → rotación del día, CRUD menú, CRUD salón, registro de empleado
SAL001   → disponibilidad de mesas + reserva con mesa asignada
SAL001   → comanda (plato + bebida)     → sale a cocina y bar
COS001   → despacha los platos          → queda 'en proceso'
BAR001   → despacha las bebidas         → queda 'lista'
SAL001   → ve la notificación (mesa, hora, platillos) y cierra la comanda
           → factura PROVISIONAL
BAR001   → crea su propia comanda de barra, con bebida y comida (la comida va a cocina)
CAJ001   → cancela la provisional (pasa a final) + cuenta separada + historial
admin    → los 5 reportes, con la comanda de la demo incluida
```

---

## Si algo sale mal

| Problema | Solución |
|---|---|
| El salonero no tiene mesas | Falta el **paso 2.1**: generar la rotación del día como admin. |
| "No se pudo conectar a la base de datos" | MySQL apagado, o la contraseña de `ConnectionDB.java` no coincide. |
| La comanda no aparece en cocina | Solo llega a cocina si tiene **platos**. Si solo tenía bebidas, buscarla en el bar. |
| La comanda no aparece en caja | La caja solo muestra comandas `lista` o `cerrada`. Falta que cocina y bar la despachen. |
| Todas las comandas salen "atrasadas" | Normal: son de la base de prueba y tienen horas viejas. Sirve para mostrar la regla de los 20 minutos. |
| No compila en otra máquina | Las librerías están en `ProyectoFinal/lib/` y vienen con el repositorio. Revisar que la plataforma Java del proyecto sea **JDK 25**. |
