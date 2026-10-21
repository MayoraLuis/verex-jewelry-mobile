<div align="center">
  <img src="app/src/main/res/drawable/logo.png" alt="Verex Jewelry Logo" width="220" />
  <h1>Verex Jewelry App (Android)</h1>
  <p><strong>Aplicación móvil nativa para la personalización, cotización y gestión de joyería fina</strong></p>
  <p>Desarrollada con <strong>Jetpack Compose</strong> y arquitectura reactiva conectada a <strong>Firebase</strong>.</p>

  <p>
    <img src="https://img.shields.io/badge/Kotlin-2.0%2B-blue?logo=kotlin" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Android-Jetpack%20Compose-green?logo=android" alt="Compose" />
    <img src="https://img.shields.io/badge/Backend-Firebase-orange?logo=firebase" alt="Firebase" />
    <img src="https://img.shields.io/badge/Etapa-3%20Avance%201-purple" alt="Etapa 3" />
  </p>
</div>

---

##  Enlaces de Entrega

<ul>
  <li><strong>Repositorio Oficial:</strong> <a href="https://github.com/MayoraLuis/verex-jewelry-mobile.git">https://github.com/MayoraLuis/verex-jewelry-mobile.git</a></li>
  <li><strong>Video Demostración (máx. 15 min):</strong> <em>(https://drive.google.com/file/d/1JPoHdLfPt-YF_VcY_dEULYapAVgTA7CD/view?usp=sharing)</em></li>
  <li><strong>Documento de Investigación (APA 7.ª edición):</strong> <em>(https://drive.google.com/file/d/1v0kN4bM2aPOel_Cti8n2TEMAwBYf9XUT/view?usp=sharing)</em></li>
</ul>

---

## Credenciales de Prueba para Evaluación

Para la revisión de la autenticación y el control de acceso basado en roles (RBAC)[cite: 7], se han configurado los siguientes accesos:

<table align="center" width="100%">
  <thead>
    <tr>
      <th align="left">Rol</th>
      <th align="left">Correo Electrónico</th>
      <th align="left">Contraseña</th>
      <th align="left">Destino de Navegación</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Administrador</strong></td>
      <td><code>admin@verex.com</code></td>
      <td><code>admin123</code></td>
      <td>Panel de Gestión y Pedidos (<code>AdminScreen</code>)</td>
    </tr>
    <tr>
      <td><strong>Cliente</strong></td>
      <td><code>cliente@verex.com</code></td>
      <td><code>123456</code></td>
      <td>Catálogo de Joyas y Cotizador (<code>HomeScreen</code>)</td>
    </tr>
  </tbody>
</table>


---

##  Arquitectura y Tecnologías

<ul>
  <li><strong>Interfaz Declarativa:</strong> Jetpack Compose con componentes desacoplados y Material Design 3.</li>
  <li><strong>Navegación:</strong> Jetpack Navigation Compose con paso seguro de argumentos y control de pila (<code>popUpTo</code>).</li>
  <li><strong>Patrón de Diseño:</strong> State Hoisting con separación de responsabilidades y Repository Pattern.</li>
  <li><strong>Servicios Firebase:</strong>
    <ul>
      <li><strong>Firebase Authentication:</strong> Gestión de sesiones, registro seguro y tokens de usuario[cite: 7].</li>
      <li><strong>Cloud Firestore (NoSQL):</strong> Persistencia en la nube de perfiles de usuario, roles y pedidos[cite: 7].</li>
    </ul>
  </li>
</ul>

---

## Módulos de la Aplicación

<details open>
  <summary><strong>1. Autenticación y Control de Acceso (<code>LoginScreen</code> / <code>RegisterScreen</code>)</strong></summary>
  <ul>
    <li>Validación de formularios reactivos (correo y contraseña de al menos 6 caracteres).</li>
    <li>Indicador visual de progreso (<code>CircularProgressIndicator</code>) durante la consulta de red.</li>
    <li>Redirección condicional según el campo <code>rol</code> (<code>CLIENTE</code> vs <code>ADMINISTRADOR</code>)[cite: 7].</li>
  </ul>
</details>

<details>
  <summary><strong>2. Catálogo y Cotizador (<code>HomeScreen</code> / <code>QuoterScreen</code>)</strong></summary>
  <ul>
    <li>Exploración de piezas por categorías con filtros dinámicos.</li>
    <li>Personalización de material (Oro 18K, Plata 925), selección de gemas y texto grabado con recálculo de precio en tiempo real.</li>
  </ul>
</details>

<details>
  <summary><strong>3. Carrito y Confirmación de Pedido (<code>CartScreen</code>)</strong></summary>
  <ul>
    <li>Desglose de productos seleccionados, cálculo de totales y confirmación directa de órdenes.</li>
  </ul>
</details>

<details>
  <summary><strong>4. Panel de Administración (<code>AdminScreen</code>)</strong></summary>
  <ul>
    <li>Visualización y cambio de estados de pedidos: <em>En Fabricación</em>, <em>Empaquetado</em> y <em>Enviado</em>.</li>
  </ul>
</details>

---

## 📂 Estructura del Código Fuente

<pre>
com.verex.jewelry/
├── data/                  # Repositorios y fuentes de datos
│   └── VerexRepository.kt
├── model/                 # Entidades y data classes (Joya, Pedido, Usuario)
├── navigation/            # Rutas y grafo de navegación (AppNavigation, Screen)
├── ui/
│   ├── screens/           # Vistas Composable (Login, Register, Home, Quoter, Cart, Admin)
│   └── theme/             # Sistema de diseño, paleta de colores y tipografía
└── MainActivity.kt        # Punto de entrada de la aplicación
</pre>

---

##  Pasos para Ejecutar el Proyecto

<ol>
  <li>Clonar el repositorio:
    <pre><code>git clone https://github.com/MayoraLuis/verex-jewelry-mobile.git</code></pre>
  </li>
  <li>Abrir el proyecto en <strong>Android Studio</strong>.</li>
  <li>Verificar la presencia del archivo <code>google-services.json</code> en el directorio <code>app/</code>.</li>
  <li>Sincronizar las dependencias con Gradle (<strong>Sync Project with Gradle Files</strong>).</li>
  <li>Seleccionar un dispositivo físico o emulador e iniciar la ejecución con el botón <strong>Run 'app'</strong> (<code>Shift + F10</code>).</li>
</ol>