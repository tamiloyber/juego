package com.example.a41400475.juego;

import android.util.Log;

import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCColor3B;
import org.cocos2d.types.CCSize;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 41400475 on 15/11/2016.
 */
public class Perdiste {
    CCGLSurfaceView _vistaPerdiste;
    CCSize pantallaDispositivo;
    final ThreadLocal<Sprite> imagenFondo = new ThreadLocal<>();
    Label perdiste;

    public Perdiste(CCGLSurfaceView vistaPerdiste) {
        _vistaPerdiste = vistaPerdiste;
    }

    public void Comenzar(){
        Log.d("Comenzar", "Perdiste!");
        Director.sharedDirector().attachInView(_vistaPerdiste);
        pantallaDispositivo = Director.sharedDirector().displaySize();
        Director.sharedDirector().runWithScene(EscenaPerdiste());
    }

    private Scene EscenaPerdiste(){
        Scene escenaDevolver = Scene.node();

        CapaFondo capaFondo = new CapaFondo();
        CapaFrente capaFrente = new CapaFrente();
        escenaDevolver.addChild(capaFondo, -10);
        escenaDevolver.addChild(capaFrente, 10);

        return escenaDevolver;
    }

    class CapaFondo extends Layer {

        public CapaFondo(){
            PonerCapaFondo();
        }

        private void PonerCapaFondo(){
            imagenFondo.set(Sprite.sprite("fondo.png"));
            imagenFondo.get().setPosition(pantallaDispositivo.width/2, pantallaDispositivo.height/2);
            imagenFondo.get().runAction(ScaleBy.action(0.01f, 3.0f, 4.0f));
            super.addChild(imagenFondo.get());
        }

    }

    class CapaFrente extends Layer {

        public CapaFrente(){
            PonerCapaFrente();
        }

        private void PonerCapaFrente(){
            perdiste = Label.label("Perdiste", "Verdana", 50);
            perdiste.setString("Perdiste");
            CCColor3B color = new CCColor3B(0, 0, 0);
            perdiste.setColor(color);
            perdiste.setPosition(pantallaDispositivo.width/2, pantallaDispositivo.height/2);
            super.addChild(perdiste);

            PonerBoton();
        }

        public void PonerBoton(){
            MenuItemImage boton = MenuItemImage.item("play.png", "play.png", this, "PresionaBoton");

            boton.setPosition(pantallaDispositivo.getWidth()/2, pantallaDispositivo.getHeight()/2 - 500f);

            super.addChild(boton);
        }

        public void PresionaBoton(){
            //se presiono
            Juego juego = new Juego(_vistaPerdiste);
            juego.ComenzarJuego();
        }

    }



}
