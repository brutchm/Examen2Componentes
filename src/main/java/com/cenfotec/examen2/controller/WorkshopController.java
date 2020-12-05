package com.cenfotec.examen2.controller;


import com.cenfotec.examen2.domain.Categorias;
import com.cenfotec.examen2.domain.Tareas;
import com.cenfotec.examen2.domain.Workshop;
import com.cenfotec.examen2.service.CategoriaService;
import com.cenfotec.examen2.service.TareaService;
import com.cenfotec.examen2.service.WorkshopService;

import org.apache.poi.xwpf.usermodel.*;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class WorkshopController {

    @Autowired
    WorkshopService workshopService;

    @Autowired
    CategoriaService categoriaService;

    @Autowired
    TareaService tareaService;

    @RequestMapping("/")
    public String home(Model model) {
        return "Home";
    }

    @RequestMapping(value = "/Workshop", method = RequestMethod.GET)
    public String mostrarWorkshop(Model model){


        model.addAttribute("workshop", new Workshop());
        model.addAttribute("categoria", categoriaService.getAll());
        return "Workshop";
    }

    @RequestMapping(value = "/Workshop",  method = RequestMethod.POST)
    public String insertarAction(Workshop workshop, BindingResult result, Model model, HttpServletRequest request) {
        List<Categorias> categorias = categoriaService.getAll();
        if (categorias.size()==0){
            return "Home";
        }
        workshopService.save(workshop);

        model.addAttribute("workshop",workshopService.getAll());
        model.addAttribute("Categoria", categoriaService.getAll());
        model.addAttribute("OtroWorkshop", new Workshop());
        return "listaWorkshop";
    }

    @RequestMapping(value = "/tareasWorkshop/{id}", method = RequestMethod.GET)
    public String mostrarTareas(@PathVariable("id") long id, Model model){
        Optional<Workshop> workshop = workshopService.get(id);
         Tareas newTarea = new Tareas();
        if (workshop.isPresent()) {
            newTarea.setWorkshop(workshop.get());
            model.addAttribute("workshop",workshop.get());
            model.addAttribute("tarea",newTarea);
            return "tareasWorkshop";
        }
        return "notfound";
    }

    @RequestMapping(value = "/tareasWorkshop/{id}",  method = RequestMethod.POST)
    public String insertarTareas(@PathVariable("id") long id,Tareas tareas, BindingResult result, Model model) {
            Optional<Workshop> workshop = workshopService.get(id);
        if (workshop.isPresent()) {
            tareas.setWorkshop(workshop.get());
            tareaService.save(tareas);
            Workshop anotherWorkshop = workshop.get();

            anotherWorkshop.setDuracionTotal(tareas.getDuracion());

            workshopService.save(anotherWorkshop);



            model.addAttribute("workshop",workshopService.getAll());
            model.addAttribute("Categoria", categoriaService.getAll());
            model.addAttribute("OtroWorkshop", new Workshop());
            return "listaWorkshop";
        }
        return "Home";
    }

    @RequestMapping("/listaTareas/{id}")
    public String listarTareas(Model model, @PathVariable long id) {
        Optional<Workshop> tareasWorkshop = workshopService.get(id);
        if (tareasWorkshop.isPresent()) {
            model.addAttribute("workshop",tareasWorkshop.get());
            return "listaTareas";
        }
        return "Home";
    }


    @RequestMapping("/listaWorkshop")
    public String listar(Model model) {
        model.addAttribute("workshop",workshopService.getAll());
        model.addAttribute("Categoria", categoriaService.getAll());
        model.addAttribute("OtroWorkshop", new Workshop());
        return "listaWorkshop";
    }

    @RequestMapping(value = "/Categorias", method = RequestMethod.GET)
    public String mostrarCategorias(Model model){

        model.addAttribute("categoria", new Categorias());
        return "Categorias";
    }

    @RequestMapping(value = "/Categorias",  method = RequestMethod.POST)
    public String insertarCategoria(Categorias categorias, BindingResult result, Model model) {

        categoriaService.save(categorias);
        return "Home";
    }

    @RequestMapping("/listaCategorias")
    public String listarCategoria(Model model) {
        model.addAttribute("categoria",categoriaService.getAll());
        return "listaCategorias";
    }

    @RequestMapping(value = "/eliminadaCategorias", method = RequestMethod.DELETE)
    public String eliminarCategoria(Categorias categoria,Model model) {
        categoriaService.delete(categoria);
        model.addAttribute("categoria",categoriaService.getAll());
        return "listaCategorias";
    }

    @GetMapping("/cambioWorkshop/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Optional<Workshop> workshop = workshopService.get(id);

        if (workshop.isPresent()){
            model.addAttribute("workshop", workshop);
            model.addAttribute("categoria", categoriaService.getAll());
            return "cambioWorkshop";
        }

        return "Home";
    }

    @PostMapping("/cambioWorkshop/{id}")
    public String insertarUpdate(@PathVariable("id") long id, Workshop workshop, BindingResult result, Model model) {

        if (result.hasErrors()){
            workshop.setId(id);
            return "/cambioWorkshop/{id}";
        }
        workshopService.save(workshop);
        model.addAttribute("workshop",workshopService.getAll());
        model.addAttribute("Categoria", categoriaService.getAll());
        model.addAttribute("OtroWorkshop", new Workshop());
        return "listaWorkshop";
    }


    @RequestMapping("/listaCategoriaWorkshop")
    public String listarCategoriaWorkshop(@RequestParam String categoria, Model model) {
        model.addAttribute("categoriaWorkshop",workshopService.findCategoria(categoria));
        return "listaCategoriaWorkshop";
    }


    @RequestMapping("/listaAutorWorkshop")
    public String listarAutorWorkshop(@RequestParam String autor, Model model) {
        model.addAttribute("autorWorkshop",workshopService.findAutor(autor));
        return "listaAutorWorkshop";
    }

    @RequestMapping("/listaKeywordWorkshop")
    public String listarKeywordWorkshop(@RequestParam String keyword, Model model) {
        model.addAttribute("keywordWorkshop",workshopService.findKeywords(keyword));
        return "listaKeywordWorkshop";
    }


    @GetMapping("/cambioCategoria/{id}")
    public String showUpdateFormCategoria(@PathVariable("id") long id, Model model) {
        Optional<Categorias> categorias = categoriaService.get(id);

        if (categorias.isPresent()){
            model.addAttribute("categoria", categorias);
            return "cambioCategoria";
        }

        return "Home";
    }

    @PostMapping("/cambioCategoria/{id}")
    public String insertarUpdateCategoria(@PathVariable("id") long id, Categorias categorias, BindingResult result, Model model) {

        if (result.hasErrors()){
            categorias.setId(id);
            return "/cambioWorkshop/{id}";
        }
        categoriaService.save(categorias);
        model.addAttribute("categoria",categoriaService.getAll());
        return "listaCategorias";
    }


}
