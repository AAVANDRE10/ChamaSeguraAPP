package com.example.chamasegura

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.chamasegura.data.vm.UserViewModel
import com.example.chamasegura.utils.AuthManager
import com.example.chamasegura.utils.JwtUtils
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var userViewModel: UserViewModel
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        authManager = AuthManager(this)
        drawerLayout = findViewById(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragment_home,
                R.id.fragment_manage_profile,
                R.id.fragment_burn_history,
                R.id.fragment_contact_us,
                R.id.fragment_county_history,
                R.id.fragment_manage_users,
                R.id.fragment_manage_user
            ), drawerLayout
        )

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragment_home,
                R.id.fragment_manage_profile,
                R.id.fragment_burn_history,
                R.id.fragment_contact_us,
                R.id.fragment_county_history,
                R.id.fragment_manage_users,
                R.id.fragment_manage_user -> {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
                else -> {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
        }

        if (savedInstanceState == null) {
            navController.navigate(R.id.fragment_first_screen)
        }

        // Configurar cabeçalho do NavigationView
        setupNavigationHeader()

        // Obtenha o ID do usuário do token e carregue os dados do usuário
        val token = authManager.getToken()
        val userId = token?.let { JwtUtils.getUserIdFromToken(it) }

        if (userId != null) {
            userViewModel.getUser(userId)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_manage_profile -> navController.navigate(R.id.fragment_manage_profile)
            R.id.nav_home -> navController.navigate(R.id.fragment_home)
            R.id.nav_logout -> logout()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        authManager.clearAuthData()
        Toast.makeText(this, "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show()
        // Redirecionar para o fragment_first_screen
        navController.navigate(R.id.fragment_first_screen)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun setupNavigationHeader() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val imageView = headerView.findViewById<ImageView>(R.id.imageView)
        val textViewNIF = headerView.findViewById<TextView>(R.id.textViewNIF)
        val textViewEmail = headerView.findViewById<TextView>(R.id.textViewEmail)

        userViewModel.user.observe(this) { user ->
            user?.let {
                textViewNIF.text = it.nif.toString()
                textViewEmail.text = it.email
                if (it.photo != null) {
                    Glide.with(this)
                        .load(it.photo)
                        .into(imageView)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}