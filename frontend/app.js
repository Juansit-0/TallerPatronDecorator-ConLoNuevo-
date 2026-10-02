(function () {
  const BASE_COLORS = {
    ESPRESSO: "#3a2318",
    AMERICANO: "#5c3a22",
    LATTE: "#c9a06a",
    TEA: "#b8792e"
  };

  const BASE_CLASSES = {
    ESPRESSO: "Espresso",
    AMERICANO: "Americano",
    LATTE: "Latte",
    TEA: "Tea"
  };

  const DECORATOR_NAMES = {
    SHOT: "ExtraShotDecorator",
    MILK: "MilkDecorator",
    CARAMEL: "CaramelDecorator",
    VANILLA: "VanillaDecorator",
    WHIP: "WhippedCreamDecorator",
    HONEY: "HoneyDecorator",
    OAT: "OatMilkDecorator",
    DECAF: "DecafDecorator",
    ICED: "IcedDecorator",
    HAPPYHOUR: "HappyHourDecorator"
  };

  const state = {
    catalog: null,
    base: null,
    size: "MEDIUM",
    extras: []
  };

  const byId = function (id) {
    return document.getElementById(id);
  };

  const money = function (value) {
    return "$" + value.toFixed(2);
  };

  async function init() {
    try {
      await window.CupRender.mount(byId("cup-stage"));
      const response = await fetch("api/catalog");
      if (!response.ok) {
        throw new Error("HTTP " + response.status);
      }
      state.catalog = await response.json();
      state.base = state.catalog.drinks[0].code;
      applyUrlState();
      buildBaseOptions();
      buildSizeOptions();
      buildExtraOptions();
      setStatus("ok", "Conectado");
      render();
      await refreshHistory();
    } catch (error) {
      setStatus("error", "Sin conexión");
      showToast("No se pudo cargar el catálogo. ¿Está el servidor encendido?");
    }
  }

  function applyUrlState() {
    const params = new URLSearchParams(window.location.search);
    const base = params.get("base");
    if (base && state.catalog.drinks.some(function (item) { return item.code === base; })) {
      state.base = base;
    }
    const size = params.get("size");
    if (size && state.catalog.sizes.some(function (item) { return item.code === size; })) {
      state.size = size;
    }
    const extras = params.get("extras");
    if (extras) {
      state.extras = extras.split(",").filter(function (code) {
        return state.catalog.extras.some(function (item) { return item.code === code; });
      });
    }
  }

  function buildBaseOptions() {
    const container = byId("base-options");
    container.innerHTML = "";
    state.catalog.drinks.forEach(function (drink) {
      const button = document.createElement("button");
      button.type = "button";
      button.className = "drink-card";
      button.setAttribute("role", "radio");
      button.dataset.code = drink.code;
      button.setAttribute("aria-checked", String(drink.code === state.base));

      const swatch = document.createElement("span");
      swatch.className = "drink-card__swatch";
      swatch.style.background = BASE_COLORS[drink.code] || "#d8b98c";

      const name = document.createElement("span");
      name.className = "drink-card__name";
      name.textContent = drink.label;

      const price = document.createElement("span");
      price.className = "drink-card__price";
      price.textContent = "Base " + money(drink.price);

      button.append(swatch, name, price);
      button.addEventListener("click", function () {
        state.base = drink.code;
        syncChecked(container, state.base);
        render();
      });
      container.appendChild(button);
    });
  }

  function buildSizeOptions() {
    const container = byId("size-options");
    container.innerHTML = "";
    state.catalog.sizes.forEach(function (size) {
      const button = document.createElement("button");
      button.type = "button";
      button.className = "segment";
      button.setAttribute("role", "radio");
      button.dataset.code = size.code;
      button.setAttribute("aria-checked", String(size.code === state.size));

      const label = document.createElement("span");
      label.textContent = size.label;
      const delta = document.createElement("small");
      delta.textContent = (size.price > 0 ? "+" : "") + money(size.price);
      button.append(label, delta);

      button.addEventListener("click", function () {
        state.size = size.code;
        syncChecked(container, state.size);
        render();
      });
      container.appendChild(button);
    });
  }

  function buildExtraOptions() {
    const container = byId("extras-options");
    container.innerHTML = "";
    state.catalog.extras.forEach(function (extra) {
      const button = document.createElement("button");
      button.type = "button";
      button.className = "chip" + (extra.price < 0 ? " chip--discount" : "");
      button.dataset.code = extra.code;
      button.setAttribute("aria-pressed", String(state.extras.indexOf(extra.code) !== -1));

      const label = document.createElement("span");
      label.textContent = extra.label;
      const price = document.createElement("span");
      price.className = "chip__price";
      price.textContent = (extra.price > 0 ? "+" : "") + money(extra.price);
      button.append(label, price);

      button.addEventListener("click", function () {
        const index = state.extras.indexOf(extra.code);
        if (index === -1) {
          state.extras.push(extra.code);
        } else {
          state.extras.splice(index, 1);
        }
        button.setAttribute("aria-pressed", String(index === -1));
        render();
      });
      container.appendChild(button);
    });
  }

  function syncChecked(container, code) {
    Array.prototype.forEach.call(container.children, function (child) {
      child.setAttribute("aria-checked", String(child.dataset.code === code));
    });
  }

  function buildLayers() {
    const drink = state.catalog.drinks.find(function (item) {
      return item.code === state.base;
    });
    const size = state.catalog.sizes.find(function (item) {
      return item.code === state.size;
    });
    const layers = [{ code: drink.code, label: drink.label, price: drink.price }];
    state.extras.forEach(function (code) {
      const extra = state.catalog.extras.find(function (item) {
        return item.code === code;
      });
      layers.push({ code: extra.code, label: extra.label, price: extra.price });
    });
    layers.push({ code: size.code, label: "Tamaño " + size.label, price: size.price });
    return { layers: layers, drink: drink, size: size };
  }

  function buildExpression(extras) {
    let expression = BASE_CLASSES[state.base] || "Beverage";
    extras.forEach(function (code) {
      expression = DECORATOR_NAMES[code] + "(" + expression + ")";
    });
    const size = state.catalog.sizes.find(function (item) {
      return item.code === state.size;
    });
    return "SizeDecorator(" + expression + ", " + size.label + ")";
  }

  function render() {
    const built = buildLayers();
    const total = built.layers.reduce(function (sum, layer) {
      return sum + layer.price;
    }, 0);

    window.CupRender.render({
      baseCode: state.base,
      sizeCode: state.size,
      extras: state.extras
    });

    byId("chain-expr").textContent = buildExpression(state.extras);

    const chain = byId("chain");
    chain.innerHTML = "";
    built.layers.forEach(function (layer, index) {
      const item = document.createElement("li");
      const isOuter = index === built.layers.length - 1;
      item.className = "chain__layer"
        + (isOuter ? " chain__layer--outer" : "")
        + (layer.price < 0 ? " chain__layer--discount" : "");
      const code = document.createElement("span");
      code.className = "chain__code";
      code.textContent = layer.code;
      const name = document.createElement("span");
      name.className = "chain__name";
      name.textContent = layer.label;
      const price = document.createElement("span");
      price.className = "chain__price";
      price.textContent = money(layer.price);
      item.append(code, name, price);
      chain.appendChild(item);
    });

    renderReceipt(built.layers, total);
  }

  function renderReceipt(layers, total) {
    byId("receipt-desc").textContent = describe();
    const list = byId("receipt-lines");
    list.innerHTML = "";
    layers.forEach(function (layer) {
      const item = document.createElement("li");
      item.className = "receipt__line" + (layer.price < 0 ? " receipt__line--discount" : "");
      const label = document.createElement("span");
      label.textContent = layer.label;
      const price = document.createElement("span");
      price.textContent = money(layer.price);
      item.append(label, price);
      list.appendChild(item);
    });
    byId("receipt-total").textContent = money(total);
  }

  function describe() {
    const drink = state.catalog.drinks.find(function (item) {
      return item.code === state.base;
    });
    const size = state.catalog.sizes.find(function (item) {
      return item.code === state.size;
    });
    const parts = [drink.label];
    state.extras.forEach(function (code) {
      const extra = state.catalog.extras.find(function (item) {
        return item.code === code;
      });
      parts.push(extra.label.toLowerCase());
    });
    return parts.join(" + ") + " (" + size.label + ")";
  }

  async function placeOrder() {
    const payload = {
      base: state.base,
      size: state.size,
      extras: state.extras
    };
    try {
      const response = await fetch("api/order", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      const result = await response.json();
      if (!response.ok) {
        throw new Error(result.error || "Error del servidor");
      }
      byId("receipt-id").textContent = result.orderId;
      byId("receipt-desc").textContent = result.description;
      await refreshHistory();
      showToast("Pedido " + result.orderId + " agregado · " + money(result.total));
    } catch (error) {
      showToast("No se pudo agregar el pedido: " + error.message);
    }
  }

  async function refreshHistory() {
    try {
      const response = await fetch("api/orders");
      const data = await response.json();
      byId("history-stats").textContent = data.count + " pedidos · " + money(data.revenue);
      const list = byId("history-list");
      list.innerHTML = "";
      if (!data.orders.length) {
        const empty = document.createElement("li");
        empty.className = "history__empty";
        empty.textContent = "Todavía no hay pedidos.";
        list.appendChild(empty);
        return;
      }
      data.orders.slice().reverse().forEach(function (order) {
        const item = document.createElement("li");
        item.className = "history__item";
        const label = document.createElement("span");
        label.textContent = order.orderId + " · " + order.description;
        const total = document.createElement("strong");
        total.textContent = money(order.total);
        item.append(label, total);
        list.appendChild(item);
      });
    } catch (error) {
      showToast("No se pudo cargar el historial.");
    }
  }

  function surprise() {
    const drinks = state.catalog.drinks;
    const extras = state.catalog.extras;
    state.base = drinks[Math.floor(Math.random() * drinks.length)].code;
    state.size = state.catalog.sizes[Math.floor(Math.random() * state.catalog.sizes.length)].code;
    state.extras = [];
    const shuffled = extras.slice().sort(function () {
      return Math.random() - 0.5;
    });
    const count = Math.floor(Math.random() * 4);
    for (let i = 0; i < count; i++) {
      state.extras.push(shuffled[i].code);
    }
    syncChecked(byId("base-options"), state.base);
    syncChecked(byId("size-options"), state.size);
    Array.prototype.forEach.call(byId("extras-options").children, function (chip) {
      chip.setAttribute("aria-pressed", String(state.extras.indexOf(chip.dataset.code) !== -1));
    });
    render();
    showToast("¡Sorpresa! " + describe());
  }

  function setStatus(stateName, text) {
    const pill = byId("api-status");
    pill.dataset.state = stateName;
    pill.textContent = text;
  }

  let toastTimer = null;

  function showToast(message) {
    const toast = byId("toast");
    toast.textContent = message;
    toast.classList.add("is-visible");
    clearTimeout(toastTimer);
    toastTimer = setTimeout(function () {
      toast.classList.remove("is-visible");
    }, 2600);
  }

  byId("btn-add").addEventListener("click", placeOrder);
  byId("btn-surprise").addEventListener("click", surprise);

  init();
})();
