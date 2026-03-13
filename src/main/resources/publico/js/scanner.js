const resultado = document.getElementById("resultado");
const idEvento = document.getElementById("idEvento").value;

let scanner;
let escaneando = false; // no permite que se escanee dos veces

async function onScanSuccess(decodedText) {

    if (escaneando) return;
    escaneando = true;

    try {

        const response = await fetch("/scanner/asistencia", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                codigoQr: decodedText,
                idEvento: idEvento
            })
        });

        const data = await response.json();

        if (data.ok) {

            resultado.innerHTML = `
                <div class="alert alert-success">
                    <strong>${data.nombre}</strong><br>
                    Asistencia registrada correctamente
                </div>
            `;

        } else {

            resultado.innerHTML = `
                <div class="alert alert-danger">
                    ${data.mensaje}
                </div>
            `;

        }

    } catch (error) {

        resultado.innerHTML = `
            <div class="alert alert-danger">
                Error registrando asistencia
            </div>
        `;

    } finally {
        // pasados 3 segundos se puede escanear nuevamente
        setTimeout(() => {
            escaneando = false;
            resultado.innerHTML = "";
        }, 3000);
    }

}

scanner = new Html5QrcodeScanner(
    "reader",
    { fps: 10, qrbox: 250 }
);

scanner.render(onScanSuccess);