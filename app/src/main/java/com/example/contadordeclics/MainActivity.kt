package com.example.contadordeclics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.contadordeclics.ui.theme.ContadorDeClicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContadorDeClicsTheme { // El tema de tu app
                CounterAppWithLogic()
            }
        }
    }
}

/**
 * Componente que maneja el estado del contador.
 * Este componente es "stateful" ya que contiene la variable `count`.
 * Es el punto de "elevación de estado" para el componente hijo.
 */
@Composable
fun CounterAppWithLogic() {
    // El estado del contador se declara y recuerda en este nivel superior
    var count by remember { mutableIntStateOf(0) }

    // Llama al componente sin estado (stateless) y le pasa los datos necesarios
    CounterScreenStateless(
        count = count,
        onIncrement = {
            // Lógica de negocio separada
            count = increment(count)
        }
    )
}

/**
 * Función auxiliar que contiene la lógica para incrementar el contador.
 * Esto demuestra la separación de la lógica de negocio de la UI.
 */
fun increment(currentCount: Int): Int {
    return currentCount + 1
}

/**
 * Componente de UI sin estado (stateless).
 * Este componente solo se preocupa por cómo se ve, no por el estado en sí.
 * Recibe el estado (count) y una función para manejar eventos (onIncrement) como parámetros.
 */
@Composable
fun CounterScreenStateless(
    count: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Has pulsado $count veces",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onIncrement) {
            Text(text = "Pulsa aquí")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CounterAppWithLogicPreview() {
    ContadorDeClicsTheme {
        CounterAppWithLogic()
    }
}

// Tets