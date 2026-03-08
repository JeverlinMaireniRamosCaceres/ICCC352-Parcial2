const resultado = document.getElementById("resultado");

let scanner;

async function onScanSuccess(decodedText) {

    // detener scanner
    if (scanner) {
        scanner.clear();
    }

    try {

        const response = await fetch("/scanner/asistencia", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                codigoQr: decodedText
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

    }

}

scanner = new Html5QrcodeScanner(
    "reader",
    { fps: 10, qrbox: 250 }
);

scanner.render(onScanSuccess);