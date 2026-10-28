package com.example.button.SpecialSong.WorldExec

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.delay
import kotlin.random.Random

class WorldEngine(
    private val context: Context,
    private val mediaPlayer: MediaPlayer? = null,
    private val onAppendLog: (AnnotatedString) -> Unit,
    private val onUpdateActiveLine: (AnnotatedString?) -> Unit,
    private val onClearLogs: () -> Unit
) {
    private fun getColor(color: String): Color {
        return when (color.lowercase()) {
            "white" -> Color.White
            "green" -> Color(0xFF00FF66)
            "blue" -> Color.Cyan
            "yellow" -> Color.Yellow
            "red" -> Color.Red
            else -> Color(0xFF00FF66)
        }
    }

    private suspend fun slowType(text: String, delayMs: Long, newLine: Boolean = true, spaceNumber: Int = 0, color: String = "") {
        val colorCode = getColor(color)
        val builder = StringBuilder("[Console] ")
        for (c in text) {
            builder.append(c)
            onUpdateActiveLine(buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) { append(builder.toString()) }
            })
            delay(delayMs)
        }
        if (newLine) {
            onAppendLog(buildAnnotatedString {
                withStyle(SpanStyle(color = colorCode)) { append(builder.toString()) }
            })
            onUpdateActiveLine(null)
            repeat(spaceNumber) {
                onAppendLog(AnnotatedString(""))
            }
        }
    }

    private suspend fun animateText(animateWord: String, actorTalk: String, color: String, loops: Int, sepColor: Boolean) {
        repeat(loops) {
            val shuffled = animateWord.toCharArray().apply { shuffle() }.concatToString()
            onUpdateActiveLine(buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) { append("$actorTalk$shuffled") }
            })
            delay(50)
        }

        val result = buildAnnotatedString {
            append(actorTalk)
            if (sepColor) {
                append(" ")
                withStyle(SpanStyle(color = Color.Yellow)) { append(animateWord) }
            } else {
                withStyle(SpanStyle(color = getColor(color))) { append(animateWord) }
            }
        }
        onAppendLog(result)
        onUpdateActiveLine(null)
    }

    private suspend fun simulateLoading(message: String, delayMs: Long, barAmount: Int) {
        val totalBars = 10
        for (i in 0..totalBars) {
            val progressBar = "[" + "#".repeat(i) + "-".repeat(totalBars - i) + "]"
            onUpdateActiveLine(buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) { append("$progressBar ${i * 10}%") }
            })
            delay(delayMs)
        }
        onAppendLog(buildAnnotatedString { append(message) })
        onUpdateActiveLine(null)
    }

    private suspend fun simulateWorld(worldType: Int) {
        val totalBars = 74
        val phrases = arrayOf("Adding 'You' and 'Me'.", "Generating Universe", "Adding Stars", "Crafting Narrative.")
        onAppendLog(AnnotatedString("████████ Generating World ████████"))
        readFile("textFiles/generatingWorld$worldType.txt")
        onAppendLog(AnnotatedString("Seed ID: 03108891624980232"))

        for (i in 0..totalBars) {
            val phrase = phrases[Random.nextInt(phrases.size)]
            val progressBar = "$phrase [" + "#".repeat(i) + "-".repeat(totalBars - i) + "]"
            val percent = Math.ceil(i * 10.0 / 7.4).toInt()
            onUpdateActiveLine(buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) { append("$progressBar $percent%") }
            })
            delay(187)
        }
        onUpdateActiveLine(null)
    }

    private suspend fun simulateWorld2(worldType: Int) {
        val totalBars = 74
        val phrases = arrayOf("4$@(*&J$@", "A@$@$%%#", "$%#%.", "Alice")
        onAppendLog(AnnotatedString("Lobotomy Corp. INC - 2022"))
        readFile("textFiles/logoRhine2.txt")
        onAppendLog(AnnotatedString("4129@_(921242421"))
        onAppendLog(AnnotatedString("42@#@!#24124"))

        for (i in 0..totalBars) {
            val phrase = phrases[Random.nextInt(phrases.size)]
            val progressBar = "$phrase [" + "#".repeat(i) + "-".repeat(totalBars - i) + "]"
            val percent = Math.ceil(i * 10.0 / 7.4).toInt()
            onUpdateActiveLine(buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) { append("$progressBar $percent%") }
            })
            delay(181)
        }
        onUpdateActiveLine(null)
    }

    private suspend fun encryptWall(loopAmount: Int, wordBlock: List<String>) {
        val sleepAmount = Math.ceil(120.0 / loopAmount).toLong()
        repeat(loopAmount) {
            for (word in wordBlock) {
                onUpdateActiveLine(buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White)) { append(word) }
                })
                delay(sleepAmount)
            }
            onAppendLog(AnnotatedString(wordBlock.lastOrNull() ?: ""))
            onUpdateActiveLine(null)
        }
    }

    private suspend fun readFile(fileName: String) {
        readFileAssets(context, fileName) { line ->
            onAppendLog(AnnotatedString(line))
            delay(5)
        }
    }

    suspend fun runWorldExecuteMe() {
        animateText("Song: World.Execute(Me)", "", "white", 10, false)
        animateText("Producer: Mili", "", "white", 10, false)
        delay(2000)
        onClearLogs()

        mediaPlayer?.start()
        slowType("Switch on the Power Line", 50)
        readFile("textFiles/logoRhine.txt")
        slowType("Remember to Put on -- P R O T E C T I O N", 50, true, 2)
        onAppendLog(buildAnnotatedString {
            withStyle(SpanStyle(color = Color.Yellow)) { append(">encryptEnigma();") }
        })
        simulateLoading("Encryption Set", 20, 10)

        slowType("Lay down your pieces and let's begin objectCreation();", 75)
        animateText("[Console]Fill in my data.", "", "green", 15, false)
        readFile("textFiles/createObject.txt")
        slowType("Parameters. ", 80)

        val listy = listOf("bVJijcss", "ijcssjzpTjH9no@K-emz", "1+Rza-<cT+G\"sC6IhJci!b ", "8V!g8WFkHzm<", "9no@K-emz", "56.23.15  >> Rotors ON ")
        encryptWall(6, listy)

        slowType("Initialization.", 60)
        readFile("textFiles/getError.txt")
        animateText("world.toggleValidity(false)", "", "yellow", 6, false)
        animateText("world.toggleTelementary(false)", "", "yellow", 6, false)
        simulateLoading(" Applyting World Settings", 10, 10)

        slowType("Setup our new world", 40)
        animateText("World world = new World(5);", "", "yellow", 1, false)
        animateText("world.addThing(me);", "", "yellow", 1, false)
        animateText("world.addThing(You);", "", "yellow", 1, false)

        slowType("let's begin...", 30)
        slowType("T H E  S I M U L A T I O N ::", 45)
        onClearLogs()

        animateText("world.activateSimulation()", "", "yellow", 7, false)
        simulateWorld(1)
        readFile("textFiles/getServer.txt")
        delay(170)

        readFile("textFiles/newWorldSettings.txt")
        slowType("If I'm a set of points.", 50)
        slowType("then I will give you my dimensions", 65, false)
        animateText("getDimension();", "[Console] then I will give you my", "green", 7, true)
        readFile("textFiles/getDimension.txt")

        slowType("If I'm a circle.", 75)
        slowType("then I will give you my circumference.", 45, false)
        animateText("getCircumference();", "[Console] then I will give you my ", "green", 7, true)
        readFile("textFiles/getCircumference.txt")

        slowType("If I'm a sineWave.", 75)
        slowType("then you can sit on all my tangents", 60, false)
        animateText("getTangents()", "[Console] then you can sit on all my", "green", 8, true)
        readFile("textFiles/getTangent.txt")

        slowType("If I approach infinty", 70)
        animateText("[Console] then you can be my limitations", "", "green", 18, true)
        val infinity = listOf(" 22145432389", "    32312421424242", "     4124143545433", "       █████████", "         ███████████████", "you.appplyLimitation(me,int.max);")
        encryptWall(1, infinity)

        slowType("Switch my Current", 120)
        val ACDC = listOf("DC ---> AC", "Conve", "Converting...", "Converting.. to AC to DC", "████████████████████████████████████ 98%   ", "               Converted AC to DC               ")
        encryptWall(1, ACDC)

        slowType("And then blind my Vision", 90)
        slowType("So dizzy,", 120)
        animateText("WARNING: Setting me.toggleVision() OFF will ----", "", "yellow", 6, false)
        animateText("WARNING: Setting me.toggleVision() OFF will ----", "", "yellow", 6, false)
        slowType("So dizzy", 70)
        animateText("world.disableWarnings()", "", "yellow", 5, false)

        slowType("Oh, we can travel to... ", 80)
        slowType("AB to BC", 80)
        animateText("Year: 2332 BC", "[World] Setting Date to: ", "", 5, false)
        val timeTravel = listOf("[World] Setting Locations:████████████ ", "[World]: Setting Locations: BABY████████████ ", "[World]: Setting Locations: BABYLON, Year: 2332 B.C ")
        encryptWall(1, timeTravel)

        slowType("And we can unite to", 70)
        readFile("textFiles/getDeeply1.txt")
        slowType("soo deeply,", 80)
        animateText("me.addLover('you')", "", "yellow", 3, false)
        slowType("soo deeply", 80)
        animateText("you.addLover('me')", "", "yellow", 3, false)

        slowType("If I can", 110)
        slowType("If I can", 110)
        slowType("give you all the", 60)
        slowType("S T I M U L A T I O N S", 45)
        animateText("you.getAffections();", "[World] Applying 'You' ", "", 3, false)
        slowType("Then I can, then I can be your only S A S T I F A C T I O N", 50)
        animateText("world.removeCharacters('all','nonEssential');", "", "yellow", 3, false)
        animateText("[REDACTED]", "[World] Deleting Character: ", "", 2, false)
        animateText("[REDACTED]", "[World] Deleting Character: ", "", 2, false)

        slowType("If I can make you happy,", 65)
        slowType("I will run the world.execution();", 65, color = "red")
        animateText("Simulation already Active", "[World] ", "", 2, false)
        readFile("textFiles/newWorldSettings.txt")

        slowType("Though we are trapped in this STRANGE, strange simulation", 50)
        slowType("If I'm an eggplant", 90)
        readFile("textFiles/getEggplant.txt")
        slowType("the I will you my nutrients", 70)
        animateText("if(me is Eggplant) =>you.addAttribute(me.getAttribute('nutrients'))", "", "", 5, false)

        slowType("If I'm a tomato,", 85)
        animateText("if(me is Apple) => you.addAttribute(me.getAttribute('antioxidants'))", "", "", 5, false)
        slowType("then I will give you antioxidants", 60)
        readFile("textFiles/getTomato.txt")

        slowType("If I'm a tabby cat,", 87)
        animateText("if(me is Cat){ => you.addAttribute(me.getLanguage('meow'))", "", "", 5, false)
        slowType("then I will purr for your enjoyment", 50)
        readFile("textFiles/getCat.txt")

        slowType("If I'm the only GOD,", 40, color = "red")
        animateText("[REDACTED] as Owner", "[World] Set player ", "", 4, false)
        animateText("[REDACTED] as Owner", "[World] Set player ", "", 4, false)
        animateText("[REDACTED] as Owner", "[World] Set player ", "", 2, false)
        slowType("then you're the proof of ", 50, false)
        animateText("E X I S T A N C E", "[World] Set player", "red", 5 , false)

        slowType("Switch my gender to F to M", 100, color = "red")
        animateText("Male", "[World] Set player's gender:", "", 5, false)
        slowType("And then do whatever from AM to PM", 80, true, 1, "red")
        animateText("UTC + 8", "[World] Changing Timezone:", "", 7, false)
        animateText("4:23 P.M 22/10/2023", "[World] Updating Time: ", "", 7, false)
        slowType("Oh, switch my role S to M", 120, true, 1, "red")
        slowType("me.toggleLovable())", 65, color = "yellow")
        slowType("So we can enter the trance, the trance", 90)

        slowType("If I can,", 80)
        slowType("If I can", 80)
        slowType("if I can, feel your vibrations", 80)
        readFile("textFiles/getVibration.txt")
        slowType("then I can finally be completion", 80)
        simulateLoading("C O M P L E T I O N", 50, 10)

        slowType("Though you have left,", 70, true, 1)
        slowType("Though you have left,", 65, true, 1)
        animateText("THOUGH YOU HAVE LEFT!", "[Console] ", "red", 10, false)
        animateText("THOUGH YOU HAVE LEFT!", "[Console] ", "red", 10, false)
        animateText("THOUGH YOU HAVE LEFT!", "[Console] ", "red", 10, false)
        slowType("You have left me in isolation", 100)

        slowType("If I can,", 80)
        slowType("If I can", 80)
        slowType("Erase all the pointless fragments", 80)
        slowType("Then maybe,", 80)
        slowType("then maybe, you won't leave me so disheartened", 70, true, 1)
        slowType("Challenging your God", 100, true, 1)
        animateText("$@$%)#()#*)#*^^#( ", "[Console] ", "red", 5, false)

        slowType("You have made some ILLEGAL ARGUMENTS *", 120, color = "red")
        slowType("(*&(%#)()_%(# some ILLEGAL ARGUMENTS *", 85, color = "red")
        slowType("You ha40)(*$)_@%@$ ILLEGAL ARGUMENTS *", 85, color = "red")
        slowType("You have 78^*&(*)*$#LLEGAL ARGUMENTS *", 85, color = "red")
        slowType("You have made some ILLEGAL ARGUMENTS *", 110, color = "red")

        readFile("textFiles/newWorldSettings.txt")
        readFile("textFiles/getError.txt")
        readFile("textFiles/getError.txt")
        onClearLogs()

        slowType("EXECUTION", 110, color = "red")
        slowType("EXECUTION", 120, color = "red")
        slowType("EXECUTION", 120, color = "red")
        slowType("EXECUTION", 120, color = "red")
        slowType("EXECUTION", 120, color = "red")
        slowType("EXECUTION", 120, color = "red")
        slowType("EXECUTION", 120, color = "red")
        slowType("EXECUTION", 120, color = "red")
        slowType("EXECUTION", 110, color = "red")
        slowType("EXECUTION", 110, color = "red")
        onClearLogs()

        slowType("Ein, dos, trois, ne, fem, liu, EXECUTION", 100, color = "red")
        onClearLogs()

        slowType("If I can, if I can give them all the EXECUTION", 80, color = "red")
        slowType("Then I can, then I can be your only EXECUTION", 80, color = "red")
        slowType("If I can have you back, I will run the EXECUTION", 80, color = "red")
        readFile("textFiles/getError2.txt")
        slowType("Though we are trapped, we are trapped, ah ah ah ah", 40, color = "red")
        onClearLogs()

        slowType("I've studied, I've studied how to properly l-o-ove", 70, color = "red")
        slowType("Question me, question me I can answer all lo-o-ove", 70, color = "red")
        readFile("textFiles/getError2.txt")
        onClearLogs()

        slowType("I know the algebraic expression of lo-ove", 70, color = "red")
        slowType("Though you are free, I am trapped, trapped in l-0-o-ve", 70, color = "red")
        simulateWorld2(2)
        readFile("textFiles/getError2.txt")
        onClearLogs()

        slowType("EXECUTION", 40, color = "red")
        readFile("textFiles/getError2.txt")
        readFile("textFiles/getError2.txt")
        readFile("textFiles/getError2.txt")
        readFile("textFiles/getError2.txt")
    }
}