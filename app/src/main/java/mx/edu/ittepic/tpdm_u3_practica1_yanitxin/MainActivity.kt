 package mx.edu.ittepic.tpdm_u3_practica1_yanitxin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

 class MainActivity : AppCompatActivity() {
     var descripcion: EditText? = null
     var monto: EditText? = null
     var fechaven: EditText? = null
     var si: RadioButton? = null
     var no: RadioButton? = null
     var insertar: Button? = null
     var lista: ListView? = null

     //objeto FIRESTORE
     var baseRemota = FirebaseFirestore.getInstance()
     //objetos tipo ARREGLO DINAMICO
     var registrosRemotos = ArrayList<String>()
     var keys = ArrayList<String>()

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)

         descripcion = findViewById(R.id.desc)
         monto = findViewById(R.id.monto)
         fechaven = findViewById(R.id.fechaven)
         si = findViewById(R.id.si)
         no = findViewById(R.id.no)
         insertar = findViewById(R.id.insertar)
         lista = findViewById(R.id.lista)

//################################Insertar

         insertar?.setOnClickListener {
             //Asincronia de manera remota
             //crear registro para pasar todos los datos
             var datosInsertar = hashMapOf(
                 "descripcion" to descripcion?.text.toString(),
                 "monto" to monto?.text.toString(),
                 "fechaven" to fechaven?.text.toString(),
                 "pago" to si?.isChecked()
             )

             //conexion de bd;
             // add --> insertar, eliminar -->.document(ID).(set ,delete,get) set=actualizar,delete=eliminar,get=obtener

             baseRemota.collection("recibospagos")
                 .add(datosInsertar as Map<String, Any>) //as Map<String,Any>
                 .addOnSuccessListener {
                     //Respuesta satisfactoria de que si se inserto internet
                     Toast.makeText(this, "Inserción correcta", Toast.LENGTH_LONG)
                         .show()
                 }
                 .addOnFailureListener {
                     Toast.makeText(this, "Error en inserción", Toast.LENGTH_LONG)
                         .show()

                 }
             limpiarCampos()
         }
//#################fin de Insertar


//##############Mostra-eliminar
         baseRemota.collection("recibospagos")
             .addSnapshotListener { querySnapshot, e ->
                 if (e != null) {
                     Toast.makeText(this, "Error no se puede consultar", Toast.LENGTH_SHORT).show()
                     return@addSnapshotListener
                 }
                 registrosRemotos.clear()
                 keys.clear()
                 for (document in querySnapshot!!) {
                     var cadena = document.getString("descripcion") + "\n" +
                             document.getString("monto") +
                             document.getString("fechaven") + "--" +
                             document.getString("pago")
                     registrosRemotos.add(cadena)
                     keys.add(document.id)
                 }
                 if (registrosRemotos.size == 0) {
                     registrosRemotos.add("No hay datos para mostrar")
                 }
                 var adapter = ArrayAdapter<String>(
                     this,
                     android.R.layout.simple_list_item_1,
                     registrosRemotos
                 )
                 lista?.adapter = adapter
             }

         lista?.setOnItemClickListener { parent, view, position, id ->
             if (keys.size == 0) {
                 return@setOnItemClickListener
             }

             AlertDialog.Builder(this).setTitle("ATENCION")
                 .setMessage("¿Qué deseas hacer con : " + registrosRemotos.get(position) + " ?")
                 .setPositiveButton("Eliminar") { dialog, which ->
                     baseRemota.collection("registropagos")
                         .document(keys.get(position)).delete()  //Eliminar
                         .addOnSuccessListener {
                             Toast.makeText(this, "Evento eliminado", Toast.LENGTH_SHORT)
                         }
                         .addOnFailureListener {
                             Toast.makeText(this, "Evento no eliminado", Toast.LENGTH_SHORT)
                         }
                 }
                 .setNegativeButton("Actualizar") { dialog, which ->
                     var nuevaVentana = Intent(this, Main2Activity::class.java)
                     nuevaVentana.putExtra("id", keys.get(position))
                     startActivity(nuevaVentana)

                 }
                 .setNeutralButton("Cancelar") { dialog, which -> }
                 .show()

         }
  // fin de mostrar-eliminar
     }

     fun limpiarCampos() {
         descripcion?.setText("")
         monto?.setText("")
         fechaven?.setText("")

     }
 }
