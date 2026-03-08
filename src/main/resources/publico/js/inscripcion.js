document.addEventListener("DOMContentLoaded", () => {

    const formInscripcion = document.getElementById("formInscripcion");
    const btnInscribirme = document.getElementById("btnInscribirme");
    const mensajeInscripcion = document.getElementById("mensajeInscripcion");

    if (!formInscripcion) return;

    formInscripcion.addEventListener("submit", async function (e) {
        e.preventDefault();

        try {
            const response = await fetch(formInscripcion.action, {
                method: "POST"
            });

            const data = await response.json();

            if (data.ok) {

                mensajeInscripcion.innerHTML =
                    `<div class="alert alert-success">${data.mensaje}</div>`;

                btnInscribirme.textContent = "Ya inscrito";
                btnInscribirme.disabled = true;
                const inscritos = document.getElementById("inscritosActual");
                const cupoMax = document.getElementById("cupoMaximo");

                if (inscritos && cupoMax) {
                    let actual = parseInt(inscritos.textContent);
                    inscritos.textContent = actual + 1;
                }


            } else {

                mensajeInscripcion.innerHTML =
                    `<div class="alert alert-danger">${data.mensaje}</div>`;

            }

        } catch (error) {

            mensajeInscripcion.innerHTML =
                `<div class="alert alert-danger">Error al procesar la inscripción.</div>`;

        }
    });

});