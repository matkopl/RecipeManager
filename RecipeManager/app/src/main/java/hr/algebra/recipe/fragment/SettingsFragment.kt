package hr.algebra.recipe.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import hr.algebra.recipe.HostActivity
import hr.algebra.recipe.R
import hr.algebra.recipe.repository.RecipeSqlHelper

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val clearFavoritesPref = findPreference<Preference>("clear_favorites")
        clearFavoritesPref?.setOnPreferenceClickListener {
            clearFavorites()
            true
        }
    }

    private fun clearFavorites() {
        val dbHelper = RecipeSqlHelper(requireContext())
        dbHelper.deleteAllRecipes()
        Toast.makeText(requireContext(), "All favorites cleared!", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "recipe_count") {
            val count = sharedPreferences?.getString(key, "10")?.toIntOrNull() ?: 10
            activity?.let { (it as? HostActivity)?.updateRecipeCount(count) }

            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as? HostActivity)?.showToolbar(true)
    }
}
