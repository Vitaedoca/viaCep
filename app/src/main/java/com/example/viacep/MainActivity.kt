package com.example.viacep

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Configurações para tratamento de Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referência aos elementos do layout
        val etCep: EditText = findViewById(R.id.etCep)
        val btnBuscar: Button = findViewById(R.id.btnBuscar)
        val tvLogradouro: TextView = findViewById(R.id.tvLogradouro)
        val tvBairro: TextView = findViewById(R.id.tvBairro)
        val tvCidade: TextView = findViewById(R.id.tvCidade)
        val tvUf: TextView = findViewById(R.id.tvUf)

        // Configura o botão para buscar o CEP
        btnBuscar.setOnClickListener {
            val cep = etCep.text.toString().trim()
            if (cep.isNotEmpty()) {
                buscarCep(cep, tvLogradouro, tvBairro, tvCidade, tvUf)
            } else {
                tvLogradouro.text = "Por favor, digite um CEP válido."
                tvBairro.text = ""
                tvCidade.text = ""
                tvUf.text = ""
            }
        }
    }

    // Função para buscar o CEP usando OkHttp
    private fun buscarCep(cep: String, tvLogradouro: TextView, tvBairro: TextView, tvCidade: TextView, tvUf: TextView) {
        val url = "https://viacep.com.br/ws/$cep/json/"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    tvLogradouro.text = "Erro ao buscar o CEP: ${e.message}"
                    tvBairro.text = ""
                    tvCidade.text = ""
                    tvUf.text = ""
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonString ->
                    try {
                        val jsonObject = JSONObject(jsonString)
                        val logradouro = jsonObject.optString("logradouro", "N/A")
                        val bairro = jsonObject.optString("bairro", "N/A")
                        val localidade = jsonObject.optString("localidade", "N/A")
                        val uf = jsonObject.optString("uf", "N/A")

                        runOnUiThread {
                            tvLogradouro.text = "Logradouro: $logradouro"
                            tvBairro.text = "Bairro: $bairro"
                            tvCidade.text = "Cidade: $localidade"
                            tvUf.text = "UF: $uf"
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            tvLogradouro.text = "Erro ao processar os dados."
                            tvBairro.text = ""
                            tvCidade.text = ""
                            tvUf.text = ""
                        }
                    }
                }
            }
        })
    }
}
