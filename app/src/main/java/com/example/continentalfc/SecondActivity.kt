package com.example.continentalfc

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SecondActivity : AppCompatActivity() {

    private var scoreA = 0
    private var scoreB = 0
    private var firstHalfScoreA = 0
    private var firstHalfScoreB = 0
    private var secondHalfScoreA = 0
    private var secondHalfScoreB = 0
    private var finalizedGame = false
    private var currentFrame = "N/A"

    private var FHingame = false
    private var SHingame = false

    private lateinit var scoreTeamA: TextView
    private lateinit var scoreTeamB: TextView
    private lateinit var textViewCurrentFrame: TextView
    private lateinit var buttonStartSecondHalf: Button
    private lateinit var textViewFinalScoreQ1: TextView
    private lateinit var textViewFinalScoreQ2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        textViewCurrentFrame = findViewById(R.id.textViewCurrentFrameSecond)
        scoreTeamA = findViewById(R.id.scoreContinentalFC)
        scoreTeamB = findViewById(R.id.scoreTeamB)
        textViewFinalScoreQ1 = findViewById(R.id.textViewFinalScoreQ1)
        textViewFinalScoreQ2 = findViewById(R.id.textViewFinalScoreQ2)

        val buttonBack: Button = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener { finish() }

        val buttonIncrementA: Button = findViewById(R.id.buttonIncrementA)
        buttonIncrementA.setOnClickListener {
            if (currentFrame != "N/A") {
                scoreA++
                updateScores()
            } else {
                showStartFrameDialog()
            }
        }

        val buttonIncrementB: Button = findViewById(R.id.buttonIncrementB)
        buttonIncrementB.setOnClickListener {
            if (currentFrame != "N/A") {
                scoreB++
                updateScores()
            } else {
                showStartFrameDialog()
            }
        }

        val buttonDecrementA: Button = findViewById(R.id.buttonDecrementA)
        buttonDecrementA.setOnClickListener {
            if (currentFrame != "N/A" && scoreA > 0) {
                scoreA--
                updateScores()
            } else {
                showStartFrameDialog()
            }
        }

        val buttonDecrementB: Button = findViewById(R.id.buttonDecrementB)
        buttonDecrementB.setOnClickListener {
            if (currentFrame != "N/A" && scoreB > 0) {
                scoreB--
                updateScores()
            } else {
                showStartFrameDialog()
            }
        }

        val buttonStartFirstHalf: Button = findViewById(R.id.buttonStartFirstHalf)
        buttonStartFirstHalf.setOnClickListener {
            if (!FHingame) {
                clearScores()
                currentFrame = "Primeiro Quadro"
                FHingame = true
                SHingame = false
                updateCurrentFrameText(currentFrame)
                enableScoreButtons(true)
                buttonStartFirstHalf.isEnabled = false
            }
        }

        val buttonEndFirstHalf: Button = findViewById(R.id.buttonEndFirstHalf)
        buttonEndFirstHalf.setOnClickListener {
            firstHalfScoreA = scoreA
            firstHalfScoreB = scoreB
            finalizedGame = true
            currentFrame = "Finalizado Primeiro Quadro"
            updateCurrentFrameText(currentFrame)
            textViewFinalScoreQ1.text = "Placar Final: $firstHalfScoreA - $firstHalfScoreB"
            clearScores()
            FHingame = false
            buttonStartFirstHalf.isEnabled = false
            buttonEndFirstHalf.isEnabled = false

            buttonStartSecondHalf.isEnabled = true
        }

        buttonStartSecondHalf = findViewById(R.id.buttonStartSecondHalf)
        buttonStartSecondHalf.setOnClickListener {
            if (finalizedGame && !SHingame) {
                clearScores()
                currentFrame = "Segundo Quadro"
                SHingame = true
                updateCurrentFrameText(currentFrame)
                enableControlButtons(false)
                findViewById<Button>(R.id.buttonEndSecondHalf).isEnabled = true
            } else {
                Toast.makeText(this, "O primeiro quadro deve ser finalizado antes de iniciar o segundo.", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonEndSecondHalf: Button = findViewById(R.id.buttonEndSecondHalf)
        buttonEndSecondHalf.setOnClickListener {
            secondHalfScoreA = scoreA
            secondHalfScoreB = scoreB
            finalizedGame = true
            currentFrame = "Finalizado Segundo Quadro"
            updateCurrentFrameText(currentFrame)
            textViewFinalScoreQ2.text = "Placar Final: $secondHalfScoreA - $secondHalfScoreB"
            clearScores()
            SHingame = false
            buttonStartSecondHalf.isEnabled = false
            buttonEndSecondHalf.isEnabled = false
        }

        val buttonSendResults: Button = findViewById(R.id.buttonSendEmail)
        buttonSendResults.setOnClickListener {
            sendResults()
        }

        buttonStartSecondHalf.isEnabled = false
        updateScores()
        updateCurrentFrameText(currentFrame)
    }

    private fun updateScores() {
        scoreTeamA.text = scoreA.toString()
        scoreTeamB.text = scoreB.toString()
    }

    private fun updateCurrentFrameText(frame: String) {
        textViewCurrentFrame.text = "Quadro Atual: $frame"
    }

    private fun sendResults() {
        val subject = "Resultados do Jogo"
        val body = "Resultados:\nPrimeiro Quadro: $firstHalfScoreA - $firstHalfScoreB\n" +
                "Segundo Quadro: ${if (SHingame) "$secondHalfScoreA - $secondHalfScoreB" else "Não iniciado"}"
        val emailSender = EmailSender("a@gmail.com", "a", this)
        emailSender.execute(subject, body)
    }

    private fun clearScores() {
        scoreA = 0
        scoreB = 0
        updateScores()
    }

    private fun showStartFrameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Iniciar Quadro")
        builder.setMessage("Nenhum quadro ativo. Por favor, inicie um quadro primeiro.")
        builder.setPositiveButton("Iniciar Primeiro Quadro") { dialog, _ ->
            clearScores()
            currentFrame = "Primeiro Quadro"
            FHingame = true
            SHingame = false
            updateCurrentFrameText(currentFrame)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun enableScoreButtons(enabled: Boolean) {
        findViewById<Button>(R.id.buttonIncrementA).isEnabled = enabled
        findViewById<Button>(R.id.buttonDecrementA).isEnabled = enabled
        findViewById<Button>(R.id.buttonIncrementB).isEnabled = enabled
        findViewById<Button>(R.id.buttonDecrementB).isEnabled = enabled
    }

    private fun enableControlButtons(enabled: Boolean) {
        findViewById<Button>(R.id.buttonStartFirstHalf).isEnabled = enabled
        findViewById<Button>(R.id.buttonEndFirstHalf).isEnabled = enabled
        findViewById<Button>(R.id.buttonStartSecondHalf).isEnabled = enabled
        findViewById<Button>(R.id.buttonEndSecondHalf).isEnabled = enabled
    }

    private fun resetScreen() {
        clearScores()
        currentFrame = "N/A"
        updateCurrentFrameText(currentFrame)
        textViewFinalScoreQ1.text = "Placar Final: N/A"
        textViewFinalScoreQ2.text = "Placar Final: N/A"
        finalizedGame = false
        enableControlButtons(true)
    }

    class EmailSender(val user: String, val password: String, val context: SecondActivity) : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg params: String?): Boolean {
            val subject = params[0]
            val body = params[1]

            val props = System.getProperties()
            props["mail.smtp.host"] = "smtp.gmail.com"
            props["mail.smtp.port"] = "587"
            props["mail.smtp.starttls.enable"] = "true"
            props["mail.smtp.auth"] = "true"

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(user, password)
                }
            })

            return try {
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(user))
                message.addRecipient(Message.RecipientType.TO, InternetAddress("@gmail.com"))
                message.subject = subject
                message.setText(body)

                Transport.send(message)
                true
            } catch (e: MessagingException) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(success: Boolean) {
            if (success) {
                Toast.makeText(context, "E-mail enviado com sucesso!", Toast.LENGTH_SHORT).show()
                context.resetScreen()
            } else {
                Toast.makeText(context, "Falha ao enviar o e-mail.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
