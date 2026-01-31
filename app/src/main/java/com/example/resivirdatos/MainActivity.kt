package com.example.resivirdatos

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import java.text.Normalizer
import java.util.Calendar
//actualisacion
//Juan Jose Ortiz Ortiz 29/01/2026
//Kevin Daniel Lozada Saldibar
//Enrique Morgado Montiel
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activa el modo de pantalla completa
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Ajusta los margenes para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Referencias a los componentes de la interfaz
        val etNombre = findViewById<TextInputEditText>(R.id.etNombre)
        val etMatricula = findViewById<TextInputEditText>(R.id.etMatricula)
        val etAsignatura = findViewById<TextInputEditText>(R.id.etAsignatura)
        val etHora = findViewById<TextInputEditText>(R.id.etHora)
        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        // Configuracion del selector de fecha (DatePicker)
        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                etFecha.setText(date)
            }, year, month, day)
            datePicker.show()
        }

        // Configuracion del selector de hora (TimePicker)
        etHora.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val timePicker = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                etHora.setText(time)
            }, hour, minute, true)
            timePicker.show()
        }

        // Evento al presionar el boton de enviar
        btnEnviar.setOnClickListener {
            val nombreRaw = etNombre.text.toString().trim()
            val matricula = etMatricula.text.toString().trim()
            val asignaturaRaw = etAsignatura.text.toString().trim()
            val hora = etHora.text.toString().trim()
            val fecha = etFecha.text.toString().trim()

            // Elimina los acentos de los textos ingresados
            val nombre = quitarAcentos(nombreRaw)
            val asignatura = quitarAcentos(asignaturaRaw)

            // Validacion de datos
            when {
                // Verifica que ningun campo este vacio
                nombre.isEmpty() || matricula.isEmpty() || asignatura.isEmpty() || hora.isEmpty() || fecha.isEmpty() -> {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
                // Valida que el nombre solo tenga letras y espacios
                !validarSoloLetras(nombre) -> {
                    etNombre.error = "El nombre solo debe contener letras"
                    Toast.makeText(this, "Nombre invalido", Toast.LENGTH_SHORT).show()
                }
                // Valida que la matricula no sea mayor a 10 digitos
                matricula.length > 10 -> {
                    etMatricula.error = "No debe exceder los 10 digitos"
                    Toast.makeText(this, "La matricula es demasiado larga", Toast.LENGTH_SHORT).show()
                }
                // Valida longitud minima del nombre
                nombre.length < 3 -> {
                    etNombre.error = "El nombre es demasiado corto"
                    Toast.makeText(this, "Nombre muy corto", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Prepara el resumen de los datos para mostrar en el dialogo
                    val mensaje = """
                        Datos Ingresados:
                        Nombre: $nombre
                        Matricula: $matricula
                        Asignatura: $asignatura
                        Hora: $hora
                        Fecha: $fecha
                    """.trimIndent()

                    // Muestra el cuadro de dialogo con la informacion
                    AlertDialog.Builder(this)
                        .setTitle("Resumen de Registro")
                        .setMessage(mensaje)
                        .setPositiveButton("Aceptar") { _, _ ->
                            // Limpia los campos despues de confirmar
                            etNombre.setText("")
                            etMatricula.setText("")
                            etAsignatura.setText("")
                            etHora.setText("")
                            etFecha.setText("")
                            etNombre.error = null
                            etMatricula.error = null
                        }
                        .show()
                }
            }
        }
    }

    // Funcion para validar que el texto solo contenga letras y espacios
    private fun validarSoloLetras(texto: String): Boolean {
        return texto.all { it.isLetter() || it.isWhitespace() }
    }

    // Funcion auxiliar para remover acentos y marcas diacriticas
    private fun quitarAcentos(texto: String): String {
        val normalized = Normalizer.normalize(texto, Normalizer.Form.NFD)
        return normalized.replace(Regex("[\\p{InCombiningDiacriticalMarks}]"), "")
    }
}