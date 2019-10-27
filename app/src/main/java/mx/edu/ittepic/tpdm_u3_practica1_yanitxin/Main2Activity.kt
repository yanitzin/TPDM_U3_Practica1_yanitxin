package mx.edu.ittepic.tpdm_u3_practica1_yanitxin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class Main2Activity : AppCompatActivity() {
    var descripcionA: EditText? = null
    var montoA: EditText? = null
    var fechavenA: EditText? = null
    var siA: RadioButton? = null
    var noA: RadioButton? = null
    var regresar: Button? = null
    var editar: Button? = null

    var baseRemota=FirebaseFirestore.getInstance()
    var id=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        descripcionA = findViewById(R.id.descA)
        montoA = findViewById(R.id.montoA)
        fechavenA = findViewById(R.id.fechavenA)
        siA = findViewById(R.id.siA)
        noA = findViewById(R.id.noA)
        editar = findViewById(R.id.editar)
        regresar = findViewById(R.id.regresar)
        id = intent.extras?.getString("id").toString()

        baseRemota.collection("eventos")
            .document(id)
            .get()
            .addOnSuccessListener {
                descripcionA?.setText(it.getString("descripcion"))
                montoA?.setText(it.getString("monto"))
                fechavenA?.setText(it.getString("lugar"))

            }
            .addOnFailureListener {
                descripcionA?.setText("NULL")
                montoA?.setText("NULL")
                fechavenA?.setText("NULL")

                descripcionA?.isEnabled = false
                montoA?.isEnabled = false
                fechavenA?.isEnabled = false
                siA?.isEnabled = false
            }

        editar?.setOnClickListener() {
            var datosActualizar = hashMapOf(
                "descripcion" to descripcionA?.text.toString(),
                "fecha" to fechavenA?.text.toString(),
                "monto" to montoA?.text.toString(),
                "pagado" to siA?.isChecked()
            )

            baseRemota.collection("eventos")
                .document(id)
                .set(datosActualizar as Map<String, Any>)
                .addOnSuccessListener {
                    limpiarCampos()
                    Toast.makeText(this, "SE ACTUALIZO", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "NO SE PUDO ACTUALIZAR", Toast.LENGTH_LONG).show()
                }
        }

        regresar?.setOnClickListener { finish() }
    }
        fun limpiarCampos() {
            descripcionA?.setText("")
            montoA?.setText("")
            fechavenA?.setText("")

    }

}
