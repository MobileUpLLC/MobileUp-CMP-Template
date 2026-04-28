package ru.mobileup.template.features.pokemons.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.widget.toolbar.AppToolbar
import ru.mobileup.template.core.widget.toolbar.AppToolbarButton
import ru.mobileup.template.core.utils.plus
import ru.mobileup.template.core.widget.EmptyPlaceholder
import ru.mobileup.template.core.widget.PullRefreshLceWidget
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_toolbar_action
import ru.mobileup.template.features.generated.resources.pokemons_empty_description
import ru.mobileup.template.features.generated.resources.pokemons_select_type
import ru.mobileup.template.features.pokemons.domain.Pokemon
import ru.mobileup.template.features.pokemons.domain.PokemonType
import ru.mobileup.template.features.pokemons.domain.PokemonTypeId

@Composable
fun PokemonListUi(
    component: PokemonListComponent,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            PokemonListToolbar(component)
        }
    ) { innerPadding ->
        PokemonListContent(
            component = component,
            innerPadding = innerPadding
        )
    }
}

@Composable
private fun PokemonListToolbar(component: PokemonListComponent) {
    val selectedTypeId by component.selectedTypeId.collectAsState()

    AppToolbar(
        title = stringResource(Res.string.pokemons_select_type),
        actions = {
            AppToolbarButton(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.pokemons_dialogs_demo_toolbar_action),
                onClick = component::onDialogsDemoClick
            )
        },
        bottomContent = {
            PokemonTypesRow(
                types = component.types,
                selectedTypeId = selectedTypeId,
                onTypeClick = component::onTypeClick
            )
        }
    )
}

@Composable
private fun PokemonListContent(
    component: PokemonListComponent,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val pokemonsState by component.pokemonsState.collectAsState()

    PullRefreshLceWidget(
        state = pokemonsState,
        innerPadding = innerPadding,
        onRefresh = component::onRefresh,
        modifier = modifier
    ) { pokemons, contentPadding ->
        if (pokemons.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding + PaddingValues(vertical = 12.dp)
            ) {
                items(
                    items = pokemons,
                    key = { it.id.value }
                ) { pokemon ->
                    PokemonItem(
                        pokemon = pokemon,
                        onClick = { component.onPokemonClick(pokemon.id) }
                    )

                    if (pokemon !== pokemons.lastOrNull()) {
                        HorizontalDivider()
                    }
                }
            }
        } else {
            EmptyPlaceholder(
                modifier = Modifier.padding(contentPadding),
                description = stringResource(Res.string.pokemons_empty_description)
            )
        }
    }
}

@Composable
private fun PokemonTypesRow(
    types: List<PokemonType>,
    selectedTypeId: PokemonTypeId,
    onTypeClick: (PokemonTypeId) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        types.forEach {
            PokemonTypeItem(
                type = it,
                isSelected = it.id == selectedTypeId,
                onClick = { onTypeClick(it.id) }
            )
        }
    }
}

@Composable
private fun PokemonItem(
    pokemon: Pokemon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        text = pokemon.name
    )
}

@Preview
@Composable
private fun PokemonListUiPreview() {
    AppTheme {
        PokemonListUi(FakePokemonListComponent())
    }
}
