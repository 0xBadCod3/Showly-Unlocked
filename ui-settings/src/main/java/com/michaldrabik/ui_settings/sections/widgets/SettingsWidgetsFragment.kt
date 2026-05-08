package com.michaldrabik.ui_settings.sections.widgets

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.michaldrabik.ui_base.BaseFragment
import com.michaldrabik.ui_base.utilities.extensions.launchAndRepeatStarted
import com.michaldrabik.ui_base.utilities.extensions.onClick
import com.michaldrabik.ui_base.utilities.viewBinding
import com.michaldrabik.ui_model.PremiumFeature
import com.michaldrabik.ui_navigation.java.NavigationArgs.ARG_ITEM
import com.michaldrabik.ui_settings.R
import com.michaldrabik.ui_settings.databinding.FragmentSettingsWidgetsBinding
import com.michaldrabik.ui_settings.helpers.AppTheme
import com.michaldrabik.ui_settings.helpers.WidgetTransparency
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsWidgetsFragment : BaseFragment<SettingsWidgetsViewModel>(R.layout.fragment_settings_widgets) {

  override val viewModel by viewModels<SettingsWidgetsViewModel>()
  private val binding by viewBinding(FragmentSettingsWidgetsBinding::bind)

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?,
  ) {
    super.onViewCreated(view, savedInstanceState)
    setupView()
    launchAndRepeatStarted(
      { viewModel.uiState.collect { render(it) } },
      doAfterLaunch = { viewModel.loadSettings() },
    )
  }

  private fun setupView() {
    with(binding) {
      settingsWidgetsLabels.onClick {
        viewModel.enableWidgetsTitles(!settingsWidgetsLabelsSwitch.isChecked, requireAppContext())
      }
    }
  }

  private fun render(uiState: SettingsWidgetsUiState) {
    uiState.run {
      with(binding) {
        settings?.let {
          settingsWidgetsLabelsSwitch.isChecked = it.widgetsShowLabel
        }
        themeWidgets?.let { theme ->
          settingsWidgetsThemeValue.setText(theme.displayName)
          settingsWidgetsTheme.onClick { view ->
            if (isPremium) {
              showWidgetThemeDialog(theme)
            } else {
              openPremiumScreen(view.tag)
            }
          }
        }
        widgetsTransparency.let { transparency ->
          settingsWidgetsTransparencyValue.setText(transparency.displayName)
          settingsWidgetsTransparency.onClick { view ->
            if (isPremium) {
              showWidgetTransparencyDialog(transparency)
            } else {
              openPremiumScreen(view.tag)
            }
          }
        }
        isPremium.let {
          val alpha = if (it) 1F else 0.5F
          settingsWidgetsTheme.alpha = alpha
          settingsWidgetsThemeValue.alpha = alpha
          settingsWidgetsTransparency.alpha = alpha
          if (it) {
            settingsWidgetsThemeTitle.setCompoundDrawables(null, null, null, null)
            settingsWidgetsTransparencyTitle.setCompoundDrawables(null, null, null, null)
          }
        }
      }
    }
  }

  private fun showWidgetThemeDialog(currentTheme: AppTheme) {
    val options = AppTheme.values()
    val selected = options.indexOf(currentTheme)

    MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
      .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dialog))
      .setSingleChoiceItems(options.map { getString(it.displayName) }.toTypedArray(), selected) { dialog, index ->
        if (index != selected) {
          viewModel.setWidgetTheme(options[index], requireAppContext())
        }
        dialog.dismiss()
      }.show()
  }

  private fun showWidgetTransparencyDialog(currentTransparency: WidgetTransparency) {
    val options = WidgetTransparency.values()
    val selected = options.indexOf(currentTransparency)

    MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
      .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dialog))
      .setSingleChoiceItems(options.map { getString(it.displayName) }.toTypedArray(), selected) { dialog, index ->
        if (index != selected) {
          viewModel.setWidgetTransparency(options[index], requireAppContext())
        }
        dialog.dismiss()
      }.show()
  }

  private fun openPremiumScreen(tag: Any?) {
    val args = bundleOf()
    if (tag != null) {
      val feature = PremiumFeature.fromTag(requireContext(), tag.toString())
      feature?.let {
        args.putSerializable(ARG_ITEM, feature)
      }
    }
    navigateTo(R.id.actionSettingsFragmentToPremium, args)
  }
}
