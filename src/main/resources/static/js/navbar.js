document.addEventListener("DOMContentLoaded", () => {
  // Obtener todos los enlaces del navbar
  const navLinks = document.querySelectorAll(".navbar-nav .nav-link");

  // Obtener la URL actual
  const currentUrl = window.location.pathname;

  // Itera sobre los enlaces y marca el activo
  navLinks.forEach(link => {
    if (link.getAttribute("href") === currentUrl) {
      link.classList.add("active"); // Resalta el enlace activo
    }
  });

  // Agregar animaciones al pasar el mouse
  navLinks.forEach(link => {
    link.addEventListener("mouseenter", () => {
      link.classList.add("hover");
    });

    link.addEventListener("mouseleave", () => {
      link.classList.remove("hover");
    });
  });
});
