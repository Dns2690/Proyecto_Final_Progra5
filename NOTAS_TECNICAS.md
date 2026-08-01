# Notas técnicas — lo que hay que saber si la profe pregunta

Documento interno del equipo. **No es entregable.** Sirve para llegar a la exposición
sabiendo defender cada decisión.

La rúbrica da **45 puntos a la exposición**, repartidos así:

| Qué evalúa | Puntos | Dónde está en este documento |
|---|---|---|
| Explica las estrategias de diseño utilizadas | 5 | §1, §2 |
| Demuestra el desarrollo de todos los requerimientos | 10 | §8 y la guía de demostración |
| Presenta la importancia de las estructuras de almacenamiento | 5 | §4 |
| Explica los conceptos solicitados dentro de la aplicación | 15 | §3, §5, §6 |
| Defiende con propiedad la selección de reportes y la base de datos | 10 | §4, §7 |

---

## 1. Arquitectura: por qué está partido en capas

El proyecto está en `ProyectoFinal/src/` con estos paquetes:

```
proyectofinal/  → ProyectoFinal.java, el main. Arranca y abre el login.
vista/          → los 6 JFrame. Solo interfaz. NO tienen SQL.
dao/            → 18 clases. Todo el SQL vive aquí.
model/          → 17 POJOs, uno por tabla. Solo datos.
connection/     → ConnectionDB (conexión) y util/EncriptadorUtil (MD5).
session/        → SesionActual, guarda quién está logueado.
```

**Si pregunta "¿por qué en capas?":** para que un cambio en la base no obligue a tocar las
pantallas y al revés. Ejemplo concreto que sí pasó: se corrigió `ComidaDAO.findById` (no leía
el precio) sin tocar ni una línea de las vistas.

**Si pregunta "¿dónde está el SQL?":** solo en `dao/`. Ninguna vista tiene un `SELECT`. Se
puede comprobar en vivo buscando "SELECT" dentro de la carpeta `vista`.

**Regla que siguen todos los DAO** (mirar `ComidaDAO` como ejemplo):
- Un campo `private final ConnectionDB conexionDB = new ConnectionDB();`
- `try-with-resources` en `Connection`, `PreparedStatement` y `ResultSet` → se cierran solos.
- El `catch (SQLException)` se maneja ahí mismo y devuelve `false`, `null` o lista vacía.
- Métodos estándar: `insert`, `update`, `delete`, `findAll`, `findById`.

---

## 2. Conceptos de POO que están aplicados (y dónde señalarlos)

| Concepto | Dónde | Qué decir |
|---|---|---|
| **Encapsulamiento** | Todos los `model/` | Atributos `private` con `get`/`set`. Nadie toca el dato directo. |
| **Instanciación de objetos** | Vistas y DAO | `new ComidaDAO()`, `new Comida()`. Cada DAO instancia su propia conexión. |
| **Sobrecarga de constructores** | `model/AsignacionSeccion` | Tiene 3 constructores: vacío, con datos y con id. |
| **Colecciones genéricas** | `List<Comida>`, `Map<String,Integer>` | Los DAO devuelven `List<T>` tipada, no arreglos. |
| **Métodos estáticos** | `SesionActual`, `EncriptadorUtil` | No se instancian; se usan como utilitarios. |
| **Constante de clase** | `FacturaDAO.IVA` | `public static final BigDecimal IVA = new BigDecimal("0.13")`. |
| **Manejo de excepciones** | Todos los DAO | `try-catch` de `SQLException` sin dejar caer el programa. |
| **Herencia** | `vista/*.java` | Cada formulario `extends javax.swing.JFrame`. |
| **Clases anónimas / lambdas** | Los listeners | `btnGuardar.addActionListener(evt -> ...)`. |

**Si pregunta por `BigDecimal`:** el dinero **no** se maneja con `double` porque `double` es
binario y arrastra errores de redondeo (`0.1 + 0.2 != 0.3`). `BigDecimal` es decimal exacto, que
es lo que corresponde para plata. En la base es `DECIMAL(10,2)`, el equivalente.

---

## 3. Seguridad: el cifrado de la contraseña

- La clase es `connection/util/EncriptadorUtil.java`, método `md5()`.
- Se cifra **en Java, antes** de mandar el valor al `PreparedStatement`. La base nunca ve la
  contraseña en texto.
- Se usa en `UsuarioDAO.insert`, `UsuarioDAO.updateContrasena`, `UsuarioDAO.login` y en
  `AdministradorDAO`. Los métodos reciben la contraseña en texto y la cifran por dentro.
- **El login no compara textos, compara hashes.** No existe forma de recuperar la contraseña
  desde la base, solo verificar si coincide.

**Pregunta incómoda probable: "¿MD5 es seguro?"**
Respuesta honesta: **no** para producción. MD5 está roto desde hace años (se le encuentran
colisiones y hay tablas precalculadas para revertir contraseñas comunes). Se usó porque es lo
que pide el enunciado del curso. Lo correcto hoy sería **bcrypt, scrypt o Argon2**, que además
de ser lentos a propósito agregan un *salt* distinto por usuario, de modo que dos personas con
la misma contraseña no producen el mismo hash. Decirlo así suma, porque demuestra que se sabe
la diferencia.

**Prueba en vivo:** en la tabla `usuario`, la contraseña de `SAL001` se ve como
`32250170a0dca92d53ec9624f336ca24`, no como `pass123`.

---

## 4. Base de datos: 17 tablas y por qué están así

### Los cuatro grupos

| Grupo | Tablas |
|---|---|
| Administración | `administrador`, `tipo_usuario`, `usuario` |
| Menú | `categoria_comida`, `comida`, `categoria_bebida`, `bebida` |
| Salón | `seccion_salon`, `mesa`, `asignacion_seccion`, `reserva` |
| Operación | `comanda`, `detalle_comanda`, `proceso_cocina`, `proceso_bar`, `factura`, `detalle_factura` |

### Normalización (3FN) — cómo defenderla

- **1FN:** no hay campos repetidos ni listas dentro de una celda. Una comanda no guarda
  "2 casados, 3 cervezas" en un campo de texto: cada línea es una fila de `detalle_comanda`.
- **2FN:** toda columna depende de la llave completa. `detalle_comanda` no guarda el nombre del
  plato, solo el `id_item`; el nombre depende de `comida`, no de la comanda.
- **3FN:** no hay dependencias transitivas. `comida` guarda `id_categoria`, no el nombre de la
  categoría, porque el nombre depende de la categoría y no del plato. Por eso al renombrar una
  categoría no hay que actualizar todos los platos.

**La excepción deliberada:** `detalle_comanda.precio_unit` guarda el precio **copiado** al
momento de ordenar. Parece redundante, pero es correcto: si mañana sube el precio del casado, la
factura de ayer no debe cambiar. Esto se llama *dato histórico* y es una desnormalización
justificada. **Si la profe lo señala como error, esta es la defensa.**

### Las tres decisiones de diseño que hay que saber explicar

**a) `asignacion_seccion` — la rotación diaria**

```sql
UNIQUE(codigo_sal, fecha)   -- un salonero = una sección por día
UNIQUE(id_seccion, fecha)   -- una sección = un salonero por día
```

La regla del negocio está garantizada **por la base**, no solo por el programa. Aunque el código
tuviera un error, la base rechaza el dato inválido. El método
`AsignacionSeccionDAO.generarRotacionDiaria(fecha)` arma el día completo: cada salonero pasa a
la sección siguiente a la última que tuvo, dando la vuelta al llegar al final, y es idempotente
(si se corre dos veces el mismo día no duplica).

**b) `detalle_comanda` — relación polimórfica**

```sql
tipo_item ENUM('comida','bebida')
id_item   INT              -- sin llave foránea, a propósito
```

Una línea de comanda puede apuntar a `comida` o a `bebida`. **No hay FK** porque una misma
columna no puede referenciar dos tablas distintas. La resolución se hace en la capa DAO:
`DetalleComandaDAO.getNombreItem()` revisa el `tipo_item` y llama a `ComidaDAO.findById` o a
`BebidaDAO.findById`.

- *Ventaja:* una sola tabla de detalle, más simple de recorrer y de facturar.
- *Desventaja:* la base no protege la integridad de ese campo; si se borra una comida, quedan
  detalles apuntando a un id que ya no existe.
- *Alternativa que se pudo usar:* dos tablas (`detalle_comida` y `detalle_bebida`), con FK real
  pero duplicando toda la lógica de facturación. **Se escogió simplicidad de operación sobre
  integridad referencial en ese punto.** Ese es el argumento.

**c) `factura` + `detalle_factura` — la cuenta separada**

`detalle_factura` solo tiene `id_factura` e `id_detalle`. Es la tabla que hace posible que las
líneas de **una misma comanda** se repartan entre **varias facturas**. Sin ella, la cuenta
separada sería imposible sin duplicar el detalle.

Cómo funciona en la caja: al cargar una comanda se buscan sus facturas, se juntan los
`id_detalle` ya cobrados y se muestran solo los que faltan. Cuando no queda ninguno pendiente,
la comanda se cierra y la mesa se libera.

**Ejemplo real que está en los datos:** la comanda 15 se pagó entre dos personas → factura 7
(₡21 000) y factura 8 (₡7 000).

---

## 5. Reglas de negocio: dónde vive cada una

| Regla | Dónde está implementada |
|---|---|
| Contraseñas en MD5 | `EncriptadorUtil.md5()`, usado por `UsuarioDAO` y `AdministradorDAO` |
| Código de empleado = prefijo + últimos 3 de la cédula | `FrmAdmin.armarCodigo()` |
| IVA 13 % | `FacturaDAO.IVA` y `aplicarIvaSiFalta()` |
| Rotación diaria de secciones | `AsignacionSeccionDAO.generarRotacionDiaria()` + los UNIQUE |
| Tope de 20 minutos para servir | `FrmSalonero.minutosDeAtraso()`, `FrmCocinero`, `FrmBartender` |
| Notificación de pedido listo (mesa, hora, platillos) | `FrmSalonero.pintarNotificacion()`, `horaNotificacion()`, `queEstaListo()` |
| Factura provisional al cerrar comanda | `FrmSalonero.generarFacturaProvisional()` |
| Factura junta o separada | `FrmCajero.facturar()` y `dividirCuenta()` |
| El bar manda a cocina lo que el cliente pide de comer | `FrmBartender.crearComandaBar()` |
| Disponibilidad de mesas para reservar | `ReservaDAO.findMesasLibres()` y `proximaHoraLibre()` |
| Un salonero solo ve las mesas de su sección | `FrmSalonero.cargarSeccionYMesas()` |

### El ciclo de la factura: provisional → final

Esto vale la pena tenerlo claro porque es lo que pide el enunciado:

1. El salonero cierra la comanda de salón → se genera una **factura provisional**
   (`tipo='provisional'`, `estado='pendiente'`) con el detalle y el IVA. **No** se le crean filas en
   `detalle_factura`, porque todavía no se ha cobrado nada.
2. La comanda sigue apareciendo en la caja, porque `FrmCajero.detallesSinFacturar()` solo cuenta
   como cobrado lo que está en facturas `tipo='final'`.
3. Cuando la caja cobra, **reutiliza esa misma factura**: la pasa a `tipo='final'`,
   `estado='pagada'`, le pone el cajero y le agrega las filas de `detalle_factura`. Es literalmente
   lo que dice el enunciado: *"esta factura pasa a Caja donde será cancelada"*.
4. En cuenta separada, la primera persona reutiliza la provisional y las siguientes generan
   facturas nuevas.
5. El historial suma solo las finales, para no contar dos veces.

### La disponibilidad para reservar

`ReservaDAO.findMesasLibres(fecha, hora)` devuelve las mesas que no tienen reserva en ese momento.
Dos detalles que hay que saber defender:

- **Se asume que una reserva ocupa la mesa 2 horas** (`ReservaDAO.MINUTOS_RESERVA = 120`). Dos
  reservas se chocan si sus horas están más cerca que eso.
- **Se asume que en una mesa caben 4 personas** (`FrmSalonero.CAPACIDAD_MESA = 4`), porque la tabla
  `mesa` no tiene columna de capacidad. Con eso se calcula cuántas mesas necesita el grupo. Si la
  profe pregunta, lo correcto sería agregar `capacidad` a la tabla `mesa`.
- Si no hay campo, `proximaHoraLibre()` recorre hora por hora hasta las 22:00 buscando cuándo se
  desocupa lo necesario.
- Al guardar, la reserva **queda con mesa asignada** (la primera libre), lo que además hace que el
  reporte de personas atendidas tenga con qué cruzar.

**Ojo con un detalle de MySQL:** `TIMESTAMPDIFF(MINUTE, columna_TIME, ?)` devuelve **NULL** cuando
la columna es de tipo `TIME`. Por eso la consulta usa
`ABS(TIME_TO_SEC(TIMEDIFF(hora_reserva, ?))) < ? * 60`. Se descubrió porque el filtro no excluía
ninguna mesa.

**El tope de 20 minutos** se calcula con `Duration.between(hora_orden, LocalDateTime.now())`.
Se compara **en Java, no en SQL**, y hay una razón: el servidor MySQL de la máquina de
desarrollo está en otra zona horaria (va 6 horas adelante). Como el programa siempre escribe y
compara con la hora de Java, el cálculo es consistente. **Si se hubiera usado `NOW()` de MySQL,
el conteo saldría mal.**

**La coordinación cocina ↔ bar** es la parte más fina del sistema. Una comanda pasa a `lista`
**solo cuando ambas áreas terminaron**:

```
cocina marca lista → ¿el bar ya terminó?  sí → comanda 'lista'
                                          no → comanda 'en_proceso'
bar marca lista    → ¿la cocina ya terminó? sí → comanda 'lista'
                                            no → comanda 'en_proceso'
```

Así el salonero no recibe el aviso de servir con medio pedido listo.

---

## 6. Los estados de una comanda

```
abierta ──> en_proceso ──> lista ──> cerrada
```

| Estado | Significa | Quién lo pone |
|---|---|---|
| `abierta` | Recién generada, nadie la ha despachado | Salonero / Bartender al crearla |
| `en_proceso` | Un área terminó, falta la otra | Cocina o Bar |
| `lista` | Cocina y bar terminaron, se puede servir | Cocina o Bar (el último) |
| `cerrada` | Ya se sirvió y se cobró | Salonero al cerrar, o Caja al facturar todo |

La caja **solo muestra comandas `lista` o `cerrada`** que además tengan consumo sin facturar.
Una comanda `abierta` o `en_proceso` no se puede cobrar porque todavía está en preparación.

---

## 7. Los cinco reportes: qué mide cada uno

Todos están en `dao/ReporteDAO.java` y devuelven `Map<String,Integer>`. Se dibujan con
**JFreeChart** (`ChartFactory.createBarChart`) dentro de `pnlGrafico` en `FrmAdmin`.

| # | Reporte | Cómo lo calcula |
|---|---|---|
| 1 | Personas atendidas por día en salón | Suma `reserva.cantidad_pers` uniendo comandas de origen `salon` con reservas por mesa y fecha |
| 2 | Personas atendidas por día en bar | Igual pero con comandas de origen `bar` |
| 3 | Comandas realizadas en el bar | Cuenta comandas con `origen='bar'` por día |
| 4 | Comandas atendidas en el bar | Cuenta las que tienen registro en `proceso_bar` |
| 5 | Comandas atendidas en cocina (salón vs bar) | Agrupa `proceso_cocina` por el origen de la comanda |

**⚠ Ojo con el reporte 2.** Une `comanda` con `reserva` por `id_mesa`, y las comandas de bar
normalmente **no tienen mesa** (el cliente consume en la barra). Por eso solo muestra datos
cuando un bartender atiende una mesa que además tiene reserva ese día. En la base de
demostración ese caso está sembrado a propósito (comanda 20, mesa 6). **Si la profe pregunta por
qué ese gráfico tiene poca data, esa es la explicación honesta.**

**Por qué JFreeChart y no otra cosa:** es una biblioteca madura para gráficos en Swing, se
integra como un `JPanel` normal (`ChartPanel`) y no requiere servidor ni navegador. Está en
`ProyectoFinal/lib/` junto con su dependencia `jcommon`.

---

## 8. Requerimientos: qué está hecho y qué no

**Hay que entrar sabiendo esto.** Es peor que la profe encuentre un faltante a que uno lo
reconozca antes.

### Implementado

- Login de administrador y de empleados, con contraseña cifrada y cierre de sesión.
- CRUD de comidas, bebidas y sus categorías.
- CRUD del salón: secciones, mesas y asignación de salonero por sección.
- Registro de usuarios con el código de 3 letras + 3 dígitos.
- Restricción de acceso por rol: cada login abre solo su pantalla.
- Rotación diaria de secciones.
- Reservas con fecha, hora, cantidad de personas, niños y consulta de disponibilidad.
- Comanda de salón con captura de nombre por código de plato y bebida, hora de orden y tope de
  20 minutos.
- Envío automático a cocina y a bar según el contenido.
- Cocina y bar: cola, hora de recibido, hora de salida y cierre.
- **Notificación al salonero** con número de mesa, hora en que quedó listo y los platillos y bebidas.
- **Factura provisional** al cerrar la comanda de salón, que después se cancela en caja.
- Comanda propia del bar, sin mesa, **con bebidas y también comida** (la comida se manda a cocina).
- Caja: cuenta junta y cuenta separada, con IVA del 13 %.
- **Vista preliminar de reserva:** mesas libres a esa fecha y hora, cuántas mesas necesita el grupo,
  y a qué hora se desocupa una si no hay campo. La reserva queda con mesa asignada.
- Los 5 reportes gráficos.
- Base de datos normalizada con datos de prueba.

### Faltante o parcial — decirlo antes de que lo pregunten

| Requisito del enunciado | Estado | Qué responder |
|---|---|---|
| Capacidad de la mesa | ⚠ Supuesto | La tabla `mesa` no tiene columna de capacidad; se asume 4 personas por mesa (`FrmSalonero.CAPACIDAD_MESA`). Lo correcto sería agregar la columna. |
| Transacciones | ⚠ Limitación | Generar una comanda son varios INSERT sueltos, sin `commit`/`rollback`. Si fallara a la mitad quedaría incompleta. |

**La documentación interna está en inglés**, como pide el enunciado: los comentarios y el Javadoc
de las 45 clases se redactaron en inglés. Los **identificadores siguen en español**
(`ComandaDAO`, `findMesasLibres`, `id_comanda`) porque son los nombres del dominio y de las
columnas de la base; y los **mensajes al usuario también están en español**, que es lo que
corresponde porque el sistema lo usa personal de un restaurante en Costa Rica. Lo único en inglés
dentro del código son los comentarios, que es justo lo que se evalúa como documentación interna.

Los diagramas de base de datos (E-R y desde SQL) los aporta denis por aparte.

---

## 9. Datos del entorno

- **Java:** el proyecto compila con **JDK 25** (`javac.source=25` en `nbproject/project.properties`).
- **Base:** MySQL 8.0.46, base `restaurante_db`, usuario `root`.
- **Conexión:** `connection/ConnectionDB.java`, con URL, usuario y contraseña como constantes.
  *(Si pregunta: lo correcto sería un archivo de configuración externo, no quemarlo en el código.)*
- **Librerías** en `ProyectoFinal/lib/`, versionadas en el repositorio:
  - `mysql-connector-j-9.7.0.jar` — driver JDBC
  - `jfreechart-1.5.6.jar` + `jcommon-1.0.24.jar` — gráficos
- **Respaldo de la base:** `restaurante_db_dump.sql` en la raíz del repositorio.

---

## 10. Preguntas típicas y respuesta corta

**¿Por qué Swing y no JavaFX?**
Es lo que trae NetBeans con diseñador visual integrado y lo que se usó en el curso.

**¿Qué pasa si dos saloneros toman la misma mesa?**
La mesa queda `disponible = 0` al generar la comanda, y el combo muestra el estado. Igual, el
salonero solo ve las mesas de **su** sección, y por la restricción de la rotación dos saloneros
no pueden compartir sección el mismo día.

**¿Por qué `ConnectionDB` abre una conexión nueva cada vez?**
Por simplicidad, y porque los DAO la cierran con `try-with-resources`. En un sistema real se
usaría un *pool* de conexiones (HikariCP, por ejemplo) para no pagar el costo de abrir y cerrar
en cada consulta.

**¿Cómo evitan la inyección de SQL?**
Con `PreparedStatement` y parámetros `?`. Nunca se concatena lo que digita el usuario dentro de
la consulta.

**¿Qué pasa si se cae la base a media operación?**
Cada DAO atrapa la `SQLException`, la reporta y devuelve un valor seguro; la pantalla muestra un
mensaje. **Limitación honesta:** no hay transacciones. Al generar una comanda se insertan la
comanda, sus detalles y los procesos en operaciones separadas; si fallara a la mitad quedaría
incompleta. Lo correcto sería envolverlo en una transacción con `commit`/`rollback`.

**¿Por qué el administrador está en una tabla aparte de los usuarios?**
Porque no es un empleado del restaurante: no tiene código con prefijo, no atiende mesas y no
entra en la rotación. Mezclarlo obligaría a dejar columnas vacías.
