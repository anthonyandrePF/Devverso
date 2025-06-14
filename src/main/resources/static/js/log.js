document.addEventListener("DOMContentLoaded", async () => {
  const userDropdown = document.getElementById("userDropdown");
  const loginTrigger = document.getElementById("loginTrigger");

  if (loginTrigger) {
    loginTrigger.addEventListener("click", async (e) => {
      e.preventDefault();
      try {
        const response = await fetch("/api/usuario/autenticado");
        const data = await response.json();
        if (data.autenticado) {
          document.getElementById("usernameDisplay").textContent = data.usuario;
          mostrarModalPerfil(data.usuario);
        } else {
          document.getElementById("usernameDisplay").textContent = "Iniciar sesión";
          mostrarModalLogin();
        }
      } catch {
        mostrarModalLogin();
      }
    });
  }

  if (userDropdown) {
    userDropdown.addEventListener("click", async (e) => {
      e.preventDefault();
      const response = await fetch("/api/usuario/autenticado");
      const data = await response.json();

      if (data.autenticado) {
        document.getElementById("usernameDisplay").textContent = data.usuario;
        mostrarModalPerfil(data.usuario);
      } else {
        document.getElementById("usernameDisplay").textContent = "Iniciar sesión";
        mostrarModalLogin();
      }
    });
  }

  const urlParams = new URLSearchParams(window.location.search);
  const loginSuccess = urlParams.has("loginSuccess");
  const logoutSuccess = urlParams.has("logoutSuccess");
  const registerSuccess = urlParams.has("registerSuccess");
  const emailExists = urlParams.has("emailExists");
  const loginError = urlParams.has("loginError");

  // NUEVO: Detecta si estás en /perfil
  const isPerfilPage = window.location.pathname.startsWith("/perfil");

  // Solo mostrar modales automáticos si NO estás en /perfil
  if (!isPerfilPage) {
    if (loginSuccess) {
      try {
        const res = await fetch("/api/usuario/autenticado");
        const data = await res.json();
        const nombre = data.usuario || "usuario";
        Swal.fire("Bienvenido", `¡Hola, ${nombre}!`, "success");
        // NO mostrar modal de login aquí, aunque el parámetro esté en la URL
      } catch (err) {
        Swal.fire("Bienvenido", "¡Hola, usuario!", "success");
        // NO mostrar modal de login aquí tampoco
      }
    } else if (logoutSuccess) {
      Swal.fire("Sesión cerrada", "Has cerrado sesión con éxito", "info").then(async () => {
        // Solo mostrar modal login si el usuario NO está autenticado
        try {
          const res = await fetch("/api/usuario/autenticado");
          const data = await res.json();
          if (!data.autenticado) {
            mostrarModalLogin();
          }
        } catch {
          mostrarModalLogin();
        }
      });
    } else if (registerSuccess) {
      Swal.fire("Registro exitoso", "Ahora inicia sesión para continuar", "success").then(() => {
        mostrarModalLogin();
      });
    } else if (emailExists) {
      Swal.fire("Error", "El correo ya está registrado", "error").then(() => {
        mostrarModalRegister();
      });
    } else if (loginError) {
      // Solo mostrar modal login si el usuario NO está autenticado
      Swal.fire("Error", "Correo o contraseña incorrectos", "error").then(async () => {
        try {
          const res = await fetch("/api/usuario/autenticado");
          const data = await res.json();
          if (!data.autenticado) {
            mostrarModalLogin();
          }
        } catch {
          mostrarModalLogin();
        }
      });
    }
  }

  if (
    loginSuccess || logoutSuccess ||
    registerSuccess || emailExists || loginError
  ) {
    const cleanUrl = window.location.origin + window.location.pathname;
    window.history.replaceState({}, document.title, cleanUrl);
  }

  // NUEVO: Actualiza el nombre en el navbar al cargar la página
  try {
    const res = await fetch("/api/usuario/autenticado");
    const data = await res.json();
    const usernameDisplay = document.getElementById("usernameDisplay");
    if (usernameDisplay) {
      if (data.autenticado) {
        usernameDisplay.textContent = data.usuario;
      } else {
        usernameDisplay.textContent = "Iniciar sesión";
      }
    }
  } catch (e) {
    // En caso de error, muestra "Iniciar sesión"
    const usernameDisplay = document.getElementById("usernameDisplay");
    if (usernameDisplay) usernameDisplay.textContent = "Iniciar sesión";
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(window.location.search);
  if (params.get("loginRequired")) {
    mostrarModalLogin();
  }
});

  //  Limpiar parámetros de la URL sin recargar

function mostrarModalLogin() {
  const modalHTML = `
    <div class="modal fade" id="loginModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content bg-dark text-white">
          <div class="modal-header">
            <h5 class="modal-title">Iniciar sesión</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <form method="post" action="/login">
              <div class="mb-3">
                <label class="form-label">Correo</label>
                <input type="email" class="form-control" name="email" required />
              </div> 
              <div class="mb-3">
                <label class="form-label">Contraseña</label>
                <input type="password" class="form-control" name="password" required />
              </div>
              <button type="submit" class="btn btn-primary w-100">Ingresar</button>
            </form>
            <hr />
            <p class="text-center mt-3">¿No estás registrado?
              <a href="#" id="switchToRegister">¡Regístrate aquí!</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  `;

  document.getElementById("loginModal")?.remove();

  const wrapper = document.createElement("div");
  wrapper.innerHTML = modalHTML;
  document.body.appendChild(wrapper.firstElementChild);

  const modal = new bootstrap.Modal(document.getElementById("loginModal"));
  modal.show();

  setTimeout(() => {
    document.getElementById("switchToRegister")?.addEventListener("click", (e) => {
      e.preventDefault();
      modal.hide();
      mostrarModalRegister();
    });
  }, 200);
}

function mostrarModalRegister() {
  const modalHTML = `
    <div class="modal fade" id="registerModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content bg-dark text-white">
          <div class="modal-header">
            <h5 class="modal-title">Registrarse</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <form id="registerForm" method="post" action="/register">
              <div class="mb-3">
                <label class="form-label">Nombre</label>
                <input type="text" class="form-control" name="nombre" required />
              </div>
              <div class="mb-3">
                <label class="form-label">Correo</label>
                <input type="email" class="form-control" name="email" required />
              </div>
              <div class="mb-3">
                <label class="form-label">Contraseña</label>
                <input type="password" class="form-control" name="password" id="password" required />
              </div>
              <div class="mb-3">
                <label class="form-label">Confirmar contraseña</label>
                <input type="password" class="form-control" name="password2" id="password2" required />
                <div id="passwordError" class="text-danger mt-1" style="display:none;">Las contraseñas no coinciden</div>
              </div>
              <button type="submit" class="btn btn-success w-100">Registrarse</button>
            </form>
            <hr />
            <p class="text-center mt-3">¿Ya estás registrado?
              <a href="#" id="switchToLogin">¡Inicia sesión aquí!</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  `;

  document.getElementById("registerModal")?.remove();

  const wrapper = document.createElement("div");
  wrapper.innerHTML = modalHTML;
  document.body.appendChild(wrapper.firstElementChild);

  const modal = new bootstrap.Modal(document.getElementById("registerModal"));
  modal.show();

  setTimeout(() => {
    document.getElementById("switchToLogin")?.addEventListener("click", (e) => {
      e.preventDefault();
      modal.hide();
      mostrarModalLogin();
    });

    // Validación de contraseñas
    const form = document.getElementById("registerForm");
    form.addEventListener("submit", function (e) {
      const pass1 = document.getElementById("password").value;
      const pass2 = document.getElementById("password2").value;
      const errorDiv = document.getElementById("passwordError");
      let valid = true;
      let errorMsg = "";

      if (pass1.length < 6) {
        valid = false;
        errorMsg = "La contraseña debe tener al menos 6 caracteres";
      } else if (pass1 !== pass2) {
        valid = false;
        errorMsg = "Las contraseñas no coinciden";
      }

      if (!valid) {
        e.preventDefault();
        errorDiv.textContent = errorMsg;
        errorDiv.style.display = "block";
      } else {
        errorDiv.style.display = "none";
      }
    });
  }, 200);
}

function mostrarModalPerfil(nombre = "usuario") {
  const modalHTML = `
    <div class="modal fade" id="perfilModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content bg-dark text-white">
          <div class="modal-header">
            <h5 class="modal-title">👤 Perfil</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body text-center">
            <p>Hola, <strong>${nombre}</strong></p>
            <div class="d-flex justify-content-center gap-2">
              <form method="post" action="/logout">
                <button type="submit" class="btn btn-danger mt-2">Cerrar sesión</button>
              </form>
              <a href="/perfil" class="btn btn-info mt-2">Ver Perfil</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  `;

  document.getElementById("perfilModal")?.remove();

  const wrapper = document.createElement("div");
  wrapper.innerHTML = modalHTML;
  document.body.appendChild(wrapper.firstElementChild);

  const modal = new bootstrap.Modal(document.getElementById("perfilModal"));
  modal.show();
}
