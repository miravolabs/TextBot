package miravolabs.textbot.textbot

import androidx.navigation.toRoute
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import miravolabs.textbot.textbot.ui.theme.DarkSlate
import miravolabs.textbot.textbot.ui.theme.PoppinsFontFamily
import miravolabs.textbot.textbot.ui.theme.PrimaryBlack
import miravolabs.textbot.textbot.ui.theme.PrimaryBlue
import miravolabs.textbot.textbot.ui.theme.PrimaryWhite
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableIntStateOf

@Serializable
sealed interface TextBotNav{
    @Serializable
    data object HomeScreen : TextBotNav
    @Serializable
    data class MainScreen(
        val text : String,
        val count : Int,
        val indexOption : Int
    ) : TextBotNav
}
@Preview
@Composable
fun TextBotAppNavigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = TextBotNav.HomeScreen
    ){
        composable<TextBotNav.HomeScreen>{
            Home(onNavigateToMainScreen = { inputText, inputAmount,inputIndexOption ->
                navController.navigate(
                    TextBotNav.MainScreen(
                        text = inputText,
                        count = inputAmount ,
                        indexOption = inputIndexOption
                    ))
                { launchSingleTop = true }
            })
        }
        composable<TextBotNav.MainScreen>{ backStackEntry ->
            val route = backStackEntry.toRoute<TextBotNav.MainScreen>()
            MainScreen(
                repeatedText = route.text,
                count = route.count,
                indexOption = route.indexOption,
                onNavigateBackToHome = {
                    if(navController.previousBackStackEntry!= null)
                    navController.popBackStack()
                }
            )
        }
    }
}
@Composable
fun Home(onNavigateToMainScreen :(String, Int, Int) -> Unit){
    var textError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var text by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    var selectedIndexOption by remember { mutableIntStateOf(0) }
    val indexOptions = listOf("None","Left","Right")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryWhite),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(scrollState)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
            ){
                Image(
                    painterResource(R.drawable.icon),
                     contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                    )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Multiply your text instantly and effortlessly",
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Medium,
                color = DarkSlate,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = text,
                onValueChange = {
                    if(it.length <= 50){
                        text = it
                        if(textError != null) textError = null
                    } else{
                        textError = "Character limit exceeded (Max 50)"
                    }
                        },
                label = { Text("Text", fontFamily = PoppinsFontFamily)},
                placeholder = {Text("Type or paste your text here...", fontFamily = PoppinsFontFamily)},
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = DarkSlate,
                    focusedLabelColor = PrimaryBlue,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = PrimaryBlack,
                    unfocusedLabelColor = DarkSlate,
                    focusedPlaceholderColor = DarkSlate,
                    unfocusedPlaceholderColor = DarkSlate,
                    focusedTextColor = PrimaryBlack,
                    unfocusedTextColor = DarkSlate,
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red,
                    errorSupportingTextColor = Color.Red
                ),
                maxLines = 2,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                isError = textError != null,
                supportingText = {
                    if (textError != null) {
                        Text(text = textError!!, fontFamily = PoppinsFontFamily)
                    } else{
                        Text(text = "${text.length}/50", fontFamily = PoppinsFontFamily)
                    }
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                    if (amountError != null) amountError = null },
                isError = amountError != null,
                supportingText = {
                    if (amountError != null) {
                        Text(text = amountError!!, fontFamily = PoppinsFontFamily)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(1f),
                label = { Text("Repetition Count", fontFamily = PoppinsFontFamily)},
                placeholder = { Text("e.g., 100", fontFamily = PoppinsFontFamily)},
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                trailingIcon = {
                    if(amount.isNotEmpty()){
                        IconButton(onClick = {amount = ""}) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = PrimaryBlack
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = DarkSlate,
                    cursorColor = PrimaryBlack,
                    focusedTextColor = PrimaryBlack,
                    unfocusedTextColor = PrimaryBlack,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = DarkSlate,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedPlaceholderColor = DarkSlate,
                    unfocusedPlaceholderColor = DarkSlate,
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red,
                    errorSupportingTextColor = Color.Red
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    textError = null
                    amountError = null

                    var hasError = false

                    if (text.trim().isEmpty()){
                        textError = "Text field cannot be empty"
                        hasError = true
                    } else if (text.length>50){
                        textError = "Character limit exceeded (Max 50)"
                        hasError = true
                    }

                    if (amount.trim().isEmpty()){
                        amountError = "Please specify the repetition count"
                        hasError = true
                    } else {
                        val count = amount.toIntOrNull()
                        if(count == null || count<= 0){
                            amountError = "Enter a number greater than 0"
                            hasError = true
                        } else if (count > 10000 ){
                            amountError = "Maximum limit is 10,000 repetitions"
                            hasError = true
                        }
                    }
                    if (!hasError){
                        onNavigateToMainScreen(text,amount.toInt(),selectedIndexOption)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = PrimaryWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Generate Repeated Text",
                    fontFamily = PoppinsFontFamily
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F7FF)),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp)
            ){
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Numbering Style (Index)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = PoppinsFontFamily,
                        color = DarkSlate
                    )
                    Spacer(modifier= Modifier.height(12.dp))
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        indexOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = indexOptions.size
                                ),
                                onClick = {selectedIndexOption = index},
                                selected = selectedIndexOption == index,
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = PrimaryBlue,
                                    activeContentColor = PrimaryWhite,
                                    inactiveContainerColor = Color.Transparent,
                                    inactiveContentColor = DarkSlate
                                )
                            ) {
                                Text(
                                    text = label,
                                    fontFamily = PoppinsFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedIndexOption == index ) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    repeatedText : String,
    count : Int,
    indexOption: Int,
    onNavigateBackToHome : () -> Unit){

    fun formatIndexedText(index: Int): String{
        return when (indexOption){
            1 -> "${index + 1 }.$repeatedText"
            2 -> "$repeatedText.${index + 1 }"
            else -> repeatedText
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var menuExpanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TextBot",
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBackToHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {menuExpanded = true}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false}
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "App Info",
                                    fontFamily = PoppinsFontFamily
                                )},
                            onClick = {
                                showDialog = true
                                menuExpanded = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryWhite
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(12.dp),
                    containerColor = DarkSlate,
                    contentColor = PrimaryWhite,
                    shape = RoundedCornerShape(12.dp),
                    action = {
                        data.visuals.actionLabel?.let{ actionText ->
                            androidx.compose.material3.TextButton(
                                onClick = {data.performAction()}
                            ) {
                                Text(
                                    text = actionText,
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryBlue,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                ){
                    Text(
                        text = data.visuals.message,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = PrimaryWhite,
                        fontSize = 14.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PrimaryWhite),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Repeated Text is Ready",
                    fontFamily = PoppinsFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkSlate
                )
                Spacer(modifier = Modifier.height(18.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 12.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryWhite
                    ),
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = "Copy Output",
                                color = PrimaryBlue,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE3F2FD))
                            ){
                                IconButton(onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                    scope.launch {
                                        val fullText = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                                            buildString {
                                                repeat(count) { index ->
                                                    append(formatIndexedText(index))
                                                    append("\n")
                                                }
                                            }.trimEnd()
                                        }

                                        clipboardManager.setText(AnnotatedString(fullText))

                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        snackbarHostState.showSnackbar(
                                            message = "Text successfully copied to clipboard!",
                                            actionLabel = "OK",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.CopyAll,
                                        contentDescription = null,
                                        tint = PrimaryBlue
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFEEEEEE))
                                .fillMaxWidth(1f)
                                .padding(horizontal = 8.dp)
                                .weight(1f),
                            contentAlignment = Alignment.TopStart
                        ){
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(
                                    count = count,
                                    key = { index -> index }
                                ){
                                    index ->
                                    Text(
                                        text = formatIndexedText(index),
                                        fontFamily = PoppinsFontFamily,
                                        fontSize = 15.sp,
                                        color = PrimaryBlack
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if(showDialog){
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = {
                    Text("App Information",
                        fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Medium
                    ) },
                text = {
                    Text(
                        text = "TextBot is a lightweight and powerful utility designed to repeat and format text seamlessly.\n" +
                                "\n" +
                                "Version: 1.1\n" +
                                "\n" +
                                "Developed by: Ahadujjaman Abir\n" +
                                "\n" +
                                "Powered by: MiravoLabs",
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Medium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {showDialog = false},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = PrimaryWhite
                        )
                    ){
                        Text("Close", fontFamily = PoppinsFontFamily)
                    }
                }
            )
        }
    }
}