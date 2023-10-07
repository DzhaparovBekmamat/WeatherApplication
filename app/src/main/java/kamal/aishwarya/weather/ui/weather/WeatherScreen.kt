package kamal.aishwarya.weather.ui.weather

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kamal.aishwarya.weather.R
import kamal.aishwarya.weather.data.model.ForecastResponse
import kamal.aishwarya.weather.model.Forecast
import kamal.aishwarya.weather.model.Weather
import kamal.aishwarya.weather.ui.theme.WeatherTheme
import kamal.aishwarya.weather.ui.weather.components.ForecastComponent
import kamal.aishwarya.weather.ui.weather.components.HourlyComponent
import kamal.aishwarya.weather.ui.weather.components.WeatherComponent
import java.util.Locale

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    val uiState: WeatherUiState by viewModel.uiState
    WeatherScreenContent(uiState, modifier)
}

@Composable
fun WeatherScreenContent(
    uiState: WeatherUiState,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> {
            WeatherLoadingState()
        }

        uiState.errorMessage.isNotEmpty() -> {
            Text(text = "Something went wrong!")
        }

        else -> {
            WeatherSuccessState(modifier = modifier, uiState = uiState)
        }
    }
}

@Composable
private fun WeatherSuccessState(
    modifier: Modifier,
    uiState: WeatherUiState,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = uiState.weather?.name.orEmpty(),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(8.dp))

        AsyncImage(
            modifier = Modifier.size(44.dp),
            model = stringResource(
                R.string.icon_url,
                uiState.weather?.condition?.icon.toString()
            ),
            contentDescription = null,
            error = painterResource(id = R.drawable.ic_placeholder),
            placeholder = painterResource(id = R.drawable.ic_placeholder),
        )
        Text(
            text = stringResource(
                R.string.temperature_value_in_celsius,
                uiState.weather?.temperature.toString()
            ),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(
                R.string.feels_like_temperature_in_celsius,
                uiState.weather?.feelsLike.toString()
            ),
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painter = painterResource(id = R.drawable.ic_sunrise), contentDescription = null)
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = uiState.weather?.forecasts?.get(0)?.sunrise?.lowercase(Locale.US).orEmpty(),
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(painter = painterResource(id = R.drawable.ic_sunset), contentDescription = null)
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = uiState.weather?.forecasts?.get(0)?.sunset?.lowercase(Locale.US).orEmpty(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            WeatherComponent(
                modifier = Modifier.weight(1f),
                weatherValue = uiState.weather?.wind.toString(),
                weatherUnit = stringResource(R.string.kilometer_per_hour),
                iconId = R.drawable.ic_wind,
            )
            WeatherComponent(
                modifier = Modifier.weight(1f),
                weatherValue = uiState.weather?.uv.toString(),
                weatherUnit = stringResource(R.string.uv_index),
                iconId = R.drawable.ic_uv,
            )
            WeatherComponent(
                modifier = Modifier.weight(1f),
                weatherValue = uiState.weather?.humidity.toString(),
                weatherUnit = stringResource(R.string.percentage),
                iconId = R.drawable.ic_humidity,
            )
        }

        Spacer(Modifier.height(32.dp))
        Text(text = "Hourly Forecast")
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(16.dp)
        ) {
            uiState.weather?.forecasts?.get(0)?.let { forecast ->
                items(forecast.hour) { hour ->
                    HourlyComponent(
                        time = hour.time,
                        icon = hour.icon,
                        temperature = stringResource(R.string.hour_temperature, hour.temperature)
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Text(text = "Next days")
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow {
            uiState.weather?.let { weather ->
                items(weather.forecasts) { forecast ->
                    ForecastComponent(
                        date = forecast.date,
                        icon = forecast.icon,
                        minTemp = forecast.minTemp,
                        maxTemp = forecast.maxTemp,
                    )
                }
            }
        }

    }
}

@Composable
private fun WeatherLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(name = "Light Mode", showBackground = true, showSystemUi = true)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
    showBackground = true
)
@Composable
fun WeatherScreenContentPreview() {
    WeatherTheme {
        Surface {
            WeatherScreenContent(
                WeatherUiState(
                    weather = Weather(
                        25,
                        22,
                        35,
                        23,
                        ForecastResponse.Current.Condition(10, "", ""),
                        6,
                        "Munich",
                        listOf(
                            Forecast("07/10", "26", "11", "06:30 am", "06:22 pm", "", emptyList()),
                            Forecast("08/10", "23", "9", "06:35 am", "06:28 pm", "", emptyList()),
                            Forecast("09/10", "22", "10", "06:40 am", "06:32 pm", "", emptyList()),
                        )
                    )
                )
            )
        }
    }
}