// Elementos del DOM
const btnAgregar = document.getElementById('btnAgregarCarrito');
const carritoIcono = document.querySelector('.bi-cart')?.closest('a');
const carrito = document.getElementById('carrito');
const contenido = document.getElementById('carrito-contenido');
const total = document.getElementById('carrito-total');

// Inicializar el Offcanvas de Bootstrap
let offcanvas = null;
if (carrito) {
  offcanvas = new bootstrap.Offcanvas(carrito);
}

// Cargar el carrito desde localStorage al iniciar
let carritoCursos = JSON.parse(localStorage.getItem('carrito')) || [];

// Función para obtener los datos del curso desde el HTML dinámicamente
function obtenerDatosCursoDesdeHTML() {
  const nombre = document.querySelector('h1')?.textContent.trim();
  const imagen = document.querySelector('.imagen-curso img')?.getAttribute('src');
  const btn = document.getElementById('btnAgregarCarrito');
  const id = btn ? parseInt(btn.getAttribute('data-curso-id')) : null;

  // Extraer el precio con descuento (último número después de "S/")
  const precioTexto = btn?.textContent;
  const precios = precioTexto?.match(/S\/(\d+\.?\d*)/g) || [];
  const precioOferta = precios.length > 0
    ? parseFloat(precios[precios.length - 1].replace('S/', ''))
    : 0;

  return { id, nombre, imagen, precioOferta };
}

// Mostrar carrito
function mostrarCarrito() {
  if (carritoCursos.length === 0) {
    contenido.innerHTML = '<p class="text-center">Tu carrito está vacío.</p>';
    total.textContent = 'S/0.00 PEN';
    offcanvas.show();
    return;
  }

  contenido.innerHTML = carritoCursos.map((curso, index) => generarHTMLCurso(curso, index)).join('');
  actualizarTotal();
  offcanvas.show();
}

// Abrir carrito desde el ícono del navbar
if (carritoIcono) {
  carritoIcono.addEventListener('click', (e) => {
    e.preventDefault();
    mostrarCarrito();
  });
}

// Agregar curso al carrito
if (btnAgregar) {
  btnAgregar.addEventListener('click', () => {
    const cursoActual = obtenerDatosCursoDesdeHTML();

    // Obtener cursos comprados
    const cursosComprados = JSON.parse(localStorage.getItem("cursosComprados")) || [];
    if (cursosComprados.includes(cursoActual.id)) {
      Swal.fire({
        icon: "info",
        title: "Curso ya comprado",
        text: "Ya compraste este curso. No puedes volver a agregarlo a tu carrito.",
      });
      return; // No abre el carrito
    }

    // Evitar duplicados en el carrito
    const yaExiste = carritoCursos.some(c => c.id === cursoActual.id);
    if (!yaExiste) {
      carritoCursos.push(cursoActual);
      localStorage.setItem("carrito", JSON.stringify(carritoCursos));
    }

    mostrarCarrito();
  });
}

// Generar HTML de cada curso en el carrito
function generarHTMLCurso(curso, index) {
  return `
    <div class="carrito-item">
      <div class="d-flex align-items-center">
        <img src="${curso.imagen}" alt="${curso.nombre}" class="img-fluid rounded me-3" style="width: 100px; height: 60px;" />
        <div class="carrito-item-info">
          <p class="mb-1 fw-bold">${curso.nombre}</p>
          <p class="text-success">S/${curso.precioOferta.toFixed(2)} PEN</p>
        </div>
      </div>
      <i class="bi bi-trash-fill text-danger fs-5" onclick="eliminarDelCarrito(${index})"></i>
    </div>
  `;
}

// Eliminar un curso del carrito
function eliminarDelCarrito(index) {
  carritoCursos.splice(index, 1);
  localStorage.setItem('carrito', JSON.stringify(carritoCursos));
  mostrarCarrito();
}

// Calcular el total del carrito
function actualizarTotal() {
  const totalValor = carritoCursos.reduce((acc, curso) => acc + curso.precioOferta, 0);
  total.textContent = `S/${totalValor.toFixed(2)} PEN`;
}

// Importar SweetAlert2 desde CDN
const Swal = window.Swal;

// Continuar con la compra
const continuarCompraBtn = document.getElementById('continuarCompraBtn');
if (continuarCompraBtn) {
  continuarCompraBtn.addEventListener('click', async (e) => {
    e.preventDefault();

    try {
      // Verificar si el usuario está autenticado
      const res = await fetch('/api/usuario/autenticado');
      const data = await res.json();

      if (!data.autenticado) {
        Swal.fire({
          icon: 'warning',
          title: 'Debes iniciar sesión para continuar con la compra',
          confirmButtonText: 'Iniciar sesión'
        }).then((result) => {
          if (result.isConfirmed) {
            mostrarModalLogin();
          }
        });
        return;
      }

      // Usuario autenticado, revisar si el carrito tiene cursos
      if (carritoCursos.length === 0) {
        Swal.fire({
          icon: 'warning',
          title: 'Carrito vacío',
          text: 'No puedes continuar porque tu carrito está vacío. Agrega cursos para proceder al pago.'
        });
        return;
      }

      // Validar si hay cursos ya comprados en el carrito
      const cursosComprados = JSON.parse(localStorage.getItem("cursosComprados")) || [];
      const cursosNoComprados = carritoCursos.filter(curso => !cursosComprados.includes(curso.id));
      if (cursosNoComprados.length === 0) {
        Swal.fire({
          icon: 'info',
          title: 'Ya compraste todos los cursos del carrito',
          text: 'No puedes volver a comprarlos.'
        });
        return;
      }

      // Todo OK: usuario autenticado, carrito no vacío y hay cursos no comprados
      window.location.href = '/pago';

    } catch (error) {
      console.error('Error al verificar sesión:', error);
      Swal.fire('Error', 'Hubo un problema verificando tu sesión.', 'error');
    }
  });
}