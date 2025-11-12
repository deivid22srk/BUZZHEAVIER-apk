package com.buzzheavier.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.buzzheavier.app.data.model.BuzzHeavierItem
import com.buzzheavier.app.util.toReadableFileSize
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(
    viewModel: FileManagerViewModel,
    directoryId: String?,
    onNavigateToDirectory: (String) -> Unit,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentDirectory by viewModel.currentDirectory.collectAsState()
    val context = LocalContext.current
    
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showMenuForItem by remember { mutableStateOf<BuzzHeavierItem?>(null) }
    var showRenameDialog by remember { mutableStateOf<BuzzHeavierItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf<BuzzHeavierItem?>(null) }
    var showMoreMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(directoryId) {
        viewModel.loadDirectory(directoryId)
    }
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = uri.lastPathSegment ?: "file_${System.currentTimeMillis()}"
                val tempFile = File(context.cacheDir, fileName)
                
                inputStream?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                
                currentDirectory?.id?.let { dirId ->
                    viewModel.uploadFile(
                        parentDirectoryId = dirId,
                        file = tempFile,
                        onSuccess = {
                            tempFile.delete()
                        },
                        onError = { error ->
                            tempFile.delete()
                        }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentDirectory?.name ?: "BuzzHeavier",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (directoryId != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadDirectory(directoryId) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Atualizar")
                    }
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sair") },
                            onClick = {
                                showMoreMenu = false
                                viewModel.logout()
                                onLogout()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.ExitToApp, contentDescription = null)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { showCreateFolderDialog = true },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.CreateNewFolder, contentDescription = "Nova pasta")
                }
                FloatingActionButton(
                    onClick = { filePickerLauncher.launch("*/*") }
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Enviar arquivo")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is FileManagerViewModel.FileManagerUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is FileManagerViewModel.FileManagerUiState.Success -> {
                    if (state.items.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Pasta vazia",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.items) { item ->
                                FileListItem(
                                    item = item,
                                    onClick = {
                                        when (item) {
                                            is BuzzHeavierItem.Dir -> onNavigateToDirectory(item.id)
                                            is BuzzHeavierItem.File -> {}
                                        }
                                    },
                                    onLongClick = {
                                        showMenuForItem = item
                                    }
                                )
                            }
                        }
                    }
                }
                is FileManagerViewModel.FileManagerUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadDirectory(directoryId) }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
        }
    }
    
    if (showCreateFolderDialog) {
        CreateFolderDialog(
            onDismiss = { showCreateFolderDialog = false },
            onCreate = { folderName ->
                currentDirectory?.id?.let { dirId ->
                    viewModel.createDirectory(dirId, folderName) {
                        showCreateFolderDialog = false
                    }
                }
            }
        )
    }
    
    showMenuForItem?.let { item ->
        ItemOptionsMenu(
            item = item,
            onDismiss = { showMenuForItem = null },
            onRename = {
                showRenameDialog = item
                showMenuForItem = null
            },
            onDelete = {
                showDeleteDialog = item
                showMenuForItem = null
            }
        )
    }
    
    showRenameDialog?.let { item ->
        RenameDialog(
            item = item,
            onDismiss = { showRenameDialog = null },
            onRename = { newName ->
                viewModel.renameItem(
                    itemId = item.id,
                    newName = newName,
                    isDirectory = item is BuzzHeavierItem.Dir,
                    currentDirectoryId = directoryId
                )
                showRenameDialog = null
            }
        )
    }
    
    showDeleteDialog?.let { item ->
        DeleteConfirmationDialog(
            item = item,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                viewModel.deleteItem(
                    itemId = item.id,
                    isDirectory = item is BuzzHeavierItem.Dir,
                    currentDirectoryId = directoryId
                )
                showDeleteDialog = null
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListItem(
    item: BuzzHeavierItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = item.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            when (item) {
                is BuzzHeavierItem.File -> Text(item.size.toReadableFileSize())
                is BuzzHeavierItem.Dir -> Text("Pasta")
            }
        },
        leadingContent = {
            Icon(
                imageVector = when (item) {
                    is BuzzHeavierItem.Dir -> Icons.Default.Folder
                    is BuzzHeavierItem.File -> Icons.Default.InsertDriveFile
                },
                contentDescription = null,
                tint = when (item) {
                    is BuzzHeavierItem.Dir -> MaterialTheme.colorScheme.primary
                    is BuzzHeavierItem.File -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
    )
    HorizontalDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var folderName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova pasta") },
        text = {
            OutlinedTextField(
                value = folderName,
                onValueChange = { folderName = it },
                label = { Text("Nome da pasta") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(folderName) },
                enabled = folderName.isNotBlank()
            ) {
                Text("Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ItemOptionsMenu(
    item: BuzzHeavierItem,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(item.name) },
        text = {
            Column {
                TextButton(
                    onClick = onRename,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Renomear")
                }
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Deletar")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

@Composable
fun RenameDialog(
    item: BuzzHeavierItem,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var newName by remember { mutableStateOf(item.name) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Renomear") },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Novo nome") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onRename(newName) },
                enabled = newName.isNotBlank() && newName != item.name
            ) {
                Text("Renomear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    item: BuzzHeavierItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, contentDescription = null) },
        title = { Text("Confirmar exclusão") },
        text = {
            Text("Deseja realmente deletar \"${item.name}\"?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Deletar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
