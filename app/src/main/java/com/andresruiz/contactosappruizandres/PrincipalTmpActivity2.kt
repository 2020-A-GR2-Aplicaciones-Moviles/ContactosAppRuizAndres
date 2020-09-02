package com.andresruiz.contactosappruizandres

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.epnfis.contactosapp.database.ContactosSQLiteOpenHelper
import kotlinx.android.synthetic.main.activity_principal_tmp2.*
import org.json.JSONException
import org.json.JSONObject


class PrincipalTmpActivity2 : AppCompatActivity() {
    var contactos = arrayListOf<ContactoModelClass>()
    var selectedContactPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_tmp2)
        val username = intent.getStringExtra(LOGIN_KEY)
        // Get the support action bar
        val actionBar = supportActionBar
        // Set the action bar title, subtitle and elevation
        actionBar!!.title = supportActionBar?.title.toString()
        actionBar.subtitle = username.substringBefore("@")

        actionBar.elevation = 4.0F

        contactos.add(
            ContactoModelClass(
                1,
                "Victor",
                "Velepucha",
                "09911223344",
                "victor.velepucha@epn.edu.ec"
            )
        )
        contactos.add(ContactoModelClass(2, "Juan", "Perez", "0991234567", "juan.perez@epn.edu.ec"))
        contactos.add(
            ContactoModelClass(
                3,
                "Michelle",
                "Salinas",
                "0995225599",
                "michelle.salinas@epn.edu.ec"
            )
        )
        contactos.add(
            ContactoModelClass(
                4,
                "Rosa",
                "Vallejo",
                "+593995225100",
                "rosa.vallejo@epn.edu.ec"
            )
        )
        val contactoAdaptador = ContactoAdapter(this, contactos)
        listViewContacts.adapter = contactoAdaptador
        ConsultarContactos();
        listViewContacts.setOnItemClickListener { parent, view, position, id ->
            selectedContactPosition = position
            editTextUserId.setText(contactos[selectedContactPosition].userId.toString())
            editTextFirstName.setText(contactos[selectedContactPosition].firstName.toString())
            editTextLastName.setText(contactos[selectedContactPosition].lastName.toString())
            editTextPhoneNumber.setText(contactos[selectedContactPosition].phoneNumber.toString())
            editTextEmailAddress.setText(contactos[selectedContactPosition].emailAddress.toString())
        }
        buttonSave.setOnClickListener {
            val id = editTextUserId.text.toString().toInt()
            val nombre = editTextFirstName.text.toString()
            val apellido = editTextLastName.text.toString()
            val telefono = editTextPhoneNumber.text.toString()
            val email = editTextEmailAddress.text.toString()
            //contactos.add(ContactoModelClass(id,nombre,apellido,telefono, email))
            //llamar base de datos
           val respuesta = ContactosSQLiteOpenHelper(this).addContacto(
               ContactoModelClass(
                   id,
                   nombre,
                   apellido,
                   telefono,
                   email
               )
           )
            if(respuesta>-1){
                Toast.makeText(this, "Contacto añadido", Toast.LENGTH_LONG).show()
                limpiarCamposEditables()
            }
            else{
                Toast.makeText(this, "Error al grabar Contacto", Toast.LENGTH_LONG).show()

            }



        }
        buttonView.setOnClickListener {
            //
            val contactos = (ContactosSQLiteOpenHelper(this).readContacto())

            listViewContacts.adapter = ContactoAdapter(this, contactos)
            limpiarCamposEditables()
        }
        //update button
        buttonUpdate.setOnClickListener {
            contactos[selectedContactPosition].userId = editTextUserId.text.toString().toInt()
            contactos[selectedContactPosition].firstName = editTextFirstName.text.toString()
            contactos[selectedContactPosition].lastName = editTextLastName.text.toString()
            contactos[selectedContactPosition].phoneNumber = editTextPhoneNumber.text.toString()
            contactos[selectedContactPosition].emailAddress = editTextEmailAddress.text.toString()
            //listViewContacts.adapter = ContactoAdapter(this, contactos)
            val respuesta = ContactosSQLiteOpenHelper(this).updateContacto(
                ContactoModelClass(
                    contactos[selectedContactPosition].userId,
                    contactos[selectedContactPosition].firstName,
                    contactos[selectedContactPosition].lastName,
                    contactos[selectedContactPosition].phoneNumber,
                    contactos[selectedContactPosition].emailAddress
                )
            )
            if(respuesta>-1){
                Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_LONG).show()
                limpiarCamposEditables()
            }
            else{
                Toast.makeText(this, "Error al actualizar Contacto", Toast.LENGTH_LONG).show()

            }
            Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_LONG).show()
            limpiarCamposEditables()
        }
        //dialogo de alerta
        buttonLlamar.setOnClickListener{
            contactos[selectedContactPosition].phoneNumber = editTextPhoneNumber.text.toString()

            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$contactos[selectedContactPosition].phoneNumber")
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        buttonDelete.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this).setIcon(
                android.R.drawable.ic_dialog_alert
            )
            dialogBuilder.setTitle("Confirmación de Eliminación")
            dialogBuilder.setMessage("¿Esta seguro que desea eliminar el contacto ${contactos[selectedContactPosition].firstName} ${contactos[selectedContactPosition].lastName}?")
            //respuesta afirmativa
            dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
                contactos.removeAt(selectedContactPosition)
                val contactoAdaptador = ContactoAdapter(this, contactos)
                listViewContacts.adapter = contactoAdaptador
                Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_LONG).show()
                limpiarCamposEditables()
            })
            //respuesta negativa
            dialogBuilder.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, which ->
                    //pass
                })
            dialogBuilder.create().show()
        }



    }
    private fun limpiarCamposEditables() {
        editTextUserId.setText("")
        editTextFirstName.setText("")
        editTextLastName.setText("")
        editTextPhoneNumber.setText("")
        editTextEmailAddress.setText("")
    }
    fun ConsultarContactos() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.androidhive.info/contacts/"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val jsonObj = JSONObject(response.toString())
                val contacts = jsonObj.getJSONArray("contacts")
                for (i in 0 until contacts.length()) {
                    val c = contacts.getJSONObject(i)
                    val id = c.getString("id").substring(1).toInt()
                    val name = c.getString("name")
                    val nombre = name.substringBefore(" ")
                    val apellido = name.substringAfter(" ")
                    val email = c.getString("email")
                    //val address = c.getString("address")
                    //val gender = c.getString("gender")
                    val phone = c.getJSONObject("phone")
                    val mobile = phone.getString("mobile")
                    val home = phone.getString("home")
                    //val office = phone.getString("office")
                    val respuesta = ContactosSQLiteOpenHelper(this).addContacto(
                        ContactoModelClass(
                            id,
                            nombre,
                            apellido,
                            mobile,
                            email
                        )
                    )
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error to read Webservice: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.d("MYTAG",error.message)
            }
        )
        queue.add(jsonObjectRequest)
    }




}