const API_URL = "https://traductor-b8ln.onrender.com";

let audioActual = "";
let imagenActual = "";

window.onload = () => {

    document.getElementById("btnAudio").style.display = "none";
    document.getElementById("imagenPalabra").style.display = "none";

    cargarCategorias();

    document.getElementById("palabras")
        .addEventListener("change", () => {

            limpiarTraduccion();
            mostrarImagen();

        });

};

function limpiarTraduccion() {

    document.getElementById("textoTraducido").textContent = "";

    document.getElementById("btnAudio").style.display = "none";

}

async function cargarCategorias() {

    const respuesta = await fetch(`${API_URL}/categorias`);

    const categorias = await respuesta.json();

    const selectCategoria = document.getElementById("categoria");

    selectCategoria.innerHTML = "";

    categorias.forEach(categoria => {

        const option = document.createElement("option");

        option.value = categoria.id;
        option.textContent = categoria.nombre;

        selectCategoria.appendChild(option);

    });

    cargarPalabras();

    selectCategoria.addEventListener("change", () => {

        limpiarTraduccion();

        const imagen = document.getElementById("imagenPalabra");

        imagen.src = "";
        imagen.style.display = "none";

        cargarPalabras();

    });

}

async function cargarPalabras() {

    const idCategoria = document.getElementById("categoria").value;

    const respuesta = await fetch(
        `${API_URL}/palabras?idCategoria=${idCategoria}`
    );

    const palabras = await respuesta.json();

    const selectPalabras = document.getElementById("palabras");

    selectPalabras.innerHTML = "";

    palabras.forEach(palabra => {

        const option = document.createElement("option");

        option.value = palabra.id;
        option.textContent = palabra.palabra;

        selectPalabras.appendChild(option);

    });

    mostrarImagen();

}

async function mostrarImagen() {

    const idPalabra = document.getElementById("palabras").value;

    const respuesta = await fetch(
        `${API_URL}/traducir?idPalabra=${idPalabra}`
    );

    const datos = await respuesta.json();

    imagenActual = datos.imagen;

    const imagen = document.getElementById("imagenPalabra");

    if (
        imagenActual === null ||
        imagenActual === "" ||
        imagenActual === "null" ||
        imagenActual === "NULL" ||
        imagenActual === undefined
    ) {

        imagen.src = "";
        imagen.style.display = "none";

    } else {

        imagen.src = "IMAGENES/" + imagenActual;
        imagen.style.display = "block";

    }

}

async function traducir() {

    const idPalabra = document.getElementById("palabras").value;

    const respuesta = await fetch(
        `${API_URL}/traducir?idPalabra=${idPalabra}`
    );

    const datos = await respuesta.json();

    document.getElementById("textoTraducido").textContent =
        datos.traduccion;

    audioActual = datos.audio;
    imagenActual = datos.imagen;

    const btnAudio = document.getElementById("btnAudio");

    if (
        audioActual === null ||
        audioActual === "" ||
        audioActual === "null" ||
        audioActual === "NULL"
    ) {

        btnAudio.style.display = "none";

    } else {

        btnAudio.style.display = "inline-block";

    }

    mostrarImagen();

}

document.addEventListener("click", function(e) {

    if (e.target.classList.contains("audio")) {

        if (
            audioActual != null &&
            audioActual !== "" &&
            audioActual !== "null" &&
            audioActual !== "NULL"
        ) {

            const audio = new Audio(
                "AUDIOS/" + audioActual
            );

            audio.play();

        }

    }

});
