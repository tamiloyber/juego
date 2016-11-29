package com.example.a41400475.juego;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import org.cocos2d.opengl.CCGLSurfaceView;

public class MainActivity extends AppCompatActivity {

    CCGLSurfaceView vistaPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        vistaPrincipal = new CCGLSurfaceView(this);
        setContentView(vistaPrincipal);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Juego juego = new Juego(vistaPrincipal);
        juego.PantallaPrincipal();
        //juego.ComenzarJuego();
    }
}
