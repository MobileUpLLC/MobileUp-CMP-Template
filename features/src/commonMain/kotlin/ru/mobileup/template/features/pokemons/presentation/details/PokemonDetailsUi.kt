package ru.mobileup.template.features.pokemons.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.jetbrains.compose.resources.stringResource
import ru.mobileup.template.core.generated.resources.common_retry
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.theme.custom.CustomTheme
import ru.mobileup.template.core.widget.AppToolbar
import ru.mobileup.template.core.widget.AppToolbarTitleAlignment
import ru.mobileup.template.core.widget.PullRefreshLceWidget
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.pokemons_height
import ru.mobileup.template.features.generated.resources.pokemons_weight
import ru.mobileup.template.features.pokemons.presentation.list.PokemonTypeItem
import ru.mobileup.template.core.generated.resources.Res as CoreRes

@Composable
fun PokemonDetailsUi(
    component: PokemonDetailsComponent,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            PokemonDetailsToolbar(component)
        }
    ) { innerPadding ->
        PokemonDetailsContent(
            component = component,
            innerPadding = innerPadding
        )
    }
}

@Composable
private fun PokemonDetailsToolbar(component: PokemonDetailsComponent) {
    val pokemonState by component.pokemonState.collectAsState()

    AppToolbar(
        title = pokemonState.data?.name.orEmpty(),
        showBackButton = true,
        titleAlignment = AppToolbarTitleAlignment.Center
    )
}

@Composable
private fun PokemonDetailsContent(
    component: PokemonDetailsComponent,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val pokemonState by component.pokemonState.collectAsState()

    PullRefreshLceWidget(
        state = pokemonState,
        innerPadding = innerPadding,
        onRefresh = component::onRefresh,
        modifier = modifier
    ) { pokemon, contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(pokemon.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(color = CustomTheme.colors.background.screen)
            )

            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = stringResource(CoreRes.string.common_retry)
            )

            Row(
                modifier = Modifier
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pokemon.types.forEach {
                    PokemonTypeItem(type = it, isSelected = true)
                }
            }

            Row(
                modifier = Modifier.padding(top = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.pokemons_height, pokemon.height)
                )
                Text(
                    text = stringResource(Res.string.pokemons_weight, pokemon.weight)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PokemonDetailsUiPreview() {
    AppTheme {
        PokemonDetailsUi(FakePokemonDetailsComponent())
    }
}
