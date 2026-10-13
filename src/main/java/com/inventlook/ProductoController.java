package com.inventlook;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ProductoController {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final MovimientoService movimientoService;

    public ProductoController(ProductoService productoService,
                              CategoriaService categoriaService,
                              UsuarioService usuarioService,
                              MovimientoService movimientoService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.movimientoService = movimientoService;
    }

    // LISTA GENERAL + FILTRO POR ESTADO (Optimo / Stock bajo / Agotado)
    @GetMapping("/productos")
    public String mostrarProductos(
            @RequestParam(value = "buscar", required = false) String buscar,
            @RequestParam(value = "estado", required = false) String estado,
            HttpSession sesion,
            Model model) {
        model.addAttribute("listaCategorias", categoriaService.listarTodas());
        model.addAttribute("productoNuevo", new Producto());
        String nombreUsuario = (String) sesion.getAttribute("nombreUsuario");
        if (nombreUsuario == null || nombreUsuario.isBlank()) return "redirect:/login";
        List<Producto> todosProductos;
        if (estado != null && !estado.isBlank()) {
            todosProductos = productoService.buscarPorEstado(estado);
            model.addAttribute("estadoSeleccionado", estado);
        } else {
            todosProductos = productoService.buscar(buscar);
        }
        model.addAttribute("nombreUsuario", nombreUsuario);
        model.addAttribute("iniciales", nombreUsuario.substring(0, 1).toUpperCase());
        model.addAttribute("productos", todosProductos);
        model.addAttribute("totalProductos", todosProductos.size());
        model.addAttribute("filtroActivo", false);
        return "productos";
    }

    // ABRIR FORMULARIO: NUEVO PRODUCTO
    @GetMapping("/agregar-producto")
    public String mostrarFormularioAgregar(HttpSession sesion, Model model) {
        if (sesion.getAttribute("nombreUsuario") == null) return "redirect:/login";
        model.addAttribute("listaCategorias", categoriaService.listarTodas());
        model.addAttribute("producto", new Producto());
        model.addAttribute("esEdicion", false);
        return "agregar-producto";
    }

    // GUARDAR: NUEVO PRODUCTO + REGISTRA MOVIMIENTO
    @PostMapping("/guardar-producto")
    public String guardarNuevoProducto(
            Producto producto,
            HttpSession sesion,
            RedirectAttributes redir) {
        String nombreUsuario = (String) sesion.getAttribute("nombreUsuario");
        if (nombreUsuario == null) return "redirect:/login";
        if (productoService.existeCodigo(producto.getCodigo(), null)) {
            redir.addFlashAttribute("error", "El codigo " + producto.getCodigo() + " ya existe");
            return "redirect:/agregar-producto";
        }
        Usuario quienLoGuarda = usuarioService.buscarPorNombre(nombreUsuario);
        producto.setUsuario(quienLoGuarda);
        productoService.guardar(producto);

        // La cantidad del movimiento es la suma de Actual + Bodega
        int totalIngresado = 0;
        if (producto.getActual() != null) totalIngresado += producto.getActual();
        if (producto.getBodega() != null) totalIngresado += producto.getBodega();

        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setCantidad(totalIngresado);
        movimiento.setFechaHora(LocalDateTime.now());
        movimiento.setObservacion("Producto agregado al inventario");
        movimiento.setProducto(producto);
        movimiento.setUsuario(quienLoGuarda);
        movimiento.setNombreProducto(producto.getNombre());
        movimientoService.guardar(movimiento);

        redir.addFlashAttribute("exito", "Producto guardado correctamente");
        return "redirect:/productos";
    }

    // ABRIR FORMULARIO: EDITAR PRODUCTO
    @GetMapping("/editar-producto/{id}")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            HttpSession sesion,
            Model model,
            RedirectAttributes redir) {
        String nombreUsuario = (String) sesion.getAttribute("nombreUsuario");
        if (nombreUsuario == null) return "redirect:/login";
        Producto producto = productoService.buscarPorId(id);
        if (producto == null) {
            redir.addFlashAttribute("error", "El producto no existe");
            return "redirect:/productos";
        }
        model.addAttribute("listaCategorias", categoriaService.listarTodas());
        model.addAttribute("producto", producto);
        model.addAttribute("esEdicion", true);
        return "agregar-producto";
    }

    // ACTUALIZAR PRODUCTO + REGISTRA AJUSTE SI CAMBIA EL STOCK TOTAL
    @PostMapping("/actualizar-producto")
    public String actualizarProducto(
            Producto producto,
            HttpSession sesion,
            RedirectAttributes redir) {
        String nombreUsuario = (String) sesion.getAttribute("nombreUsuario");
        if (nombreUsuario == null) return "redirect:/login";
        Producto original = productoService.buscarPorId(producto.getIdProducto());
        if (original == null) {
            redir.addFlashAttribute("error", "El producto no existe");
            return "redirect:/productos";
        }
        // SOLUCION CLAVE: RECUPERAMOS EL CODIGO ORIGINAL ANTES DE GUARDAR
        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            producto.setCodigo(original.getCodigo()); // Mantiene el codigo que ya tenia
        }
        if (productoService.existeCodigo(producto.getCodigo(), producto.getIdProducto())) {
            redir.addFlashAttribute("error", "El codigo " + producto.getCodigo() + " ya esta en uso");
            return "redirect:/editar-producto/" + producto.getIdProducto();
        }

        // Calculamos la diferencia del stock TOTAL (Actual + Bodega)
        int totalOriginal = 0;
        if (original.getActual() != null) totalOriginal += original.getActual();
        if (original.getBodega() != null) totalOriginal += original.getBodega();

        int totalNuevo = 0;
        if (producto.getActual() != null) totalNuevo += producto.getActual();
        if (producto.getBodega() != null) totalNuevo += producto.getBodega();

        int diferencia = totalNuevo - totalOriginal;

        Usuario quienEdita = usuarioService.buscarPorNombre(nombreUsuario);
        producto.setUsuario(original.getUsuario());
        productoService.guardar(producto);

        if (diferencia != 0) {
            Movimiento movimiento = new Movimiento();
            movimiento.setTipoMovimiento("AJUSTE");
            movimiento.setCantidad(Math.abs(diferencia));
            movimiento.setFechaHora(LocalDateTime.now());
            movimiento.setObservacion(diferencia > 0
                    ? "Se aumento el stock en " + diferencia + " unidades"
                    : "Se disminuyo el stock en " + Math.abs(diferencia) + " unidades");
            movimiento.setProducto(producto);
            movimiento.setUsuario(quienEdita);
            movimiento.setNombreProducto(producto.getNombre());
            movimientoService.guardar(movimiento);
        }

        redir.addFlashAttribute("exito", "Producto actualizado correctamente");
        return "redirect:/productos";
    }

    // ELIMINAR PRODUCTO + REGISTRA MOVIMIENTO
    @GetMapping("/eliminar-producto/{id}")
    public String eliminarProducto(
            @PathVariable Long id,
            HttpSession sesion,
            RedirectAttributes redir) {
        String nombreUsuario = (String) sesion.getAttribute("nombreUsuario");
        if (nombreUsuario == null) return "redirect:/login";
        Producto producto = productoService.buscarPorId(id);
        if (producto == null) {
            redir.addFlashAttribute("error", "El producto no existe");
            return "redirect:/productos";
        }

        // La cantidad del movimiento es la suma de Actual + Bodega
        int totalEliminado = 0;
        if (producto.getActual() != null) totalEliminado += producto.getActual();
        if (producto.getBodega() != null) totalEliminado += producto.getBodega();

        Usuario quienElimina = usuarioService.buscarPorNombre(nombreUsuario);
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setCantidad(totalEliminado);
        movimiento.setFechaHora(LocalDateTime.now());
        movimiento.setObservacion("Producto eliminado del inventario");
        movimiento.setNombreProducto(producto.getNombre());
        movimiento.setUsuario(quienElimina);
        movimientoService.guardar(movimiento);

        productoService.eliminar(id);
        redir.addFlashAttribute("exito", "Producto eliminado correctamente");
        return "redirect:/productos";
    }

    // VER PRODUCTOS DE UNA CATEGORIA
    @GetMapping("/productos-por-categoria/{idCategoria}")
    public String mostrarProductosPorCategoria(
            @PathVariable Integer idCategoria,
            HttpSession sesion,
            Model model) {
        if (sesion.getAttribute("nombreUsuario") == null) return "redirect:/login";
        String nombreUsuario = (String) sesion.getAttribute("nombreUsuario");
        List<Producto> listaFiltrada = productoService.listarPorCategoria(idCategoria);
        Categoria catSeleccionada = categoriaService.buscarPorId(idCategoria);
        model.addAttribute("nombreUsuario", nombreUsuario);
        model.addAttribute("iniciales", nombreUsuario.substring(0, 1).toUpperCase());
        model.addAttribute("productos", listaFiltrada);
        model.addAttribute("totalProductos", listaFiltrada.size());
        model.addAttribute("categoriaSeleccionada", catSeleccionada);
        model.addAttribute("filtroActivo", true);
        return "productos";
    }

    // ALERTAS DE STOCK BAJO + PRODUCTOS SIN EXISTENCIAS
    @GetMapping("/alertas")
    public String verAlertas(HttpSession sesion, Model model) {
        String nombreUsuario = (String) sesion.getAttribute("nombreUsuario");
        if (nombreUsuario == null) return "redirect:/login";
        List<Producto> stockBajo = productoService.listarProductosStockBajo();
        List<Producto> sinExistencias = productoService.listarSinExistencias();
        model.addAttribute("nombreUsuario", nombreUsuario);
        model.addAttribute("iniciales", nombreUsuario.substring(0, 1).toUpperCase());
        model.addAttribute("alertas", stockBajo);
        model.addAttribute("sinExistencias", sinExistencias);
        return "alertas";
    }
}