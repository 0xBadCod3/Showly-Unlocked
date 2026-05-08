package com.michaldrabik.ui_settings.sections.widgets.cases

import android.content.Context
import com.michaldrabik.common.dispatchers.CoroutineDispatchers
import com.michaldrabik.repository.settings.SettingsRepository
import com.michaldrabik.repository.settings.SettingsWidgetsRepository
import com.michaldrabik.ui_base.common.WidgetsProvider
import com.michaldrabik.ui_model.Settings
import com.michaldrabik.ui_settings.helpers.AppTheme
import com.michaldrabik.ui_settings.helpers.WidgetTransparency
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class SettingsWidgetsMainCase @Inject constructor(
  private val dispatchers: CoroutineDispatchers,
  private val settingsRepository: SettingsRepository,
  private val widgetsRepository: SettingsWidgetsRepository,
) {

  suspend fun getSettings(): Settings =
    withContext(dispatchers.IO) {
      settingsRepository.load()
    }

  fun getWidgetTheme(): AppTheme = AppTheme.fromCode(widgetsRepository.widgetsTheme)

  fun getWidgetTransparency(): WidgetTransparency = WidgetTransparency.fromValue(widgetsRepository.widgetsTransparency)

  suspend fun enableWidgetsTitles(
    enable: Boolean,
    context: Context,
  ) {
    val settings = settingsRepository.load()
    settings.let {
      val new = it.copy(widgetsShowLabel = enable)
      settingsRepository.update(new)
    }
    (context.applicationContext as WidgetsProvider).run {
      requestShowsWidgetsUpdate()
      requestMoviesWidgetsUpdate()
    }
  }

  suspend fun setWidgetTransparency(
    transparency: WidgetTransparency,
    context: Context,
  ) {
    widgetsRepository.widgetsTransparency = transparency.value
    (context.applicationContext as WidgetsProvider).run {
      requestShowsWidgetsUpdate()
      requestMoviesWidgetsUpdate()
    }
  }

  suspend fun setWidgetTheme(
    theme: AppTheme,
    context: Context,
  ) {
    widgetsRepository.widgetsTheme = theme.code
    (context.applicationContext as WidgetsProvider).run {
      requestShowsWidgetsUpdate()
      requestMoviesWidgetsUpdate()
    }
  }
}
