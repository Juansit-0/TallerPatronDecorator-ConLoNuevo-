(function () {
  const CUP_URL = "assets/cup.svg";

  const LIQUID_COLORS = {
    ESPRESSO: "#3a2318",
    AMERICANO: "#5c3a22",
    LATTE: "#c9a06a",
    TEA: "#b8792e"
  };

  const SIZE_SCALE = {
    SMALL: 0.86,
    MEDIUM: 1,
    LARGE: 1.16
  };

  const TOPPING_IDS = {
    SHOT: "topping-shot",
    MILK: "topping-milk",
    CARAMEL: "topping-caramel",
    VANILLA: "topping-vanilla",
    WHIP: "topping-whip",
    HONEY: "topping-honey",
    OAT: "topping-oat",
    DECAF: "topping-decaf",
    ICED: "topping-iced",
    HAPPYHOUR: "topping-happyhour"
  };

  async function mount(container) {
    const response = await fetch(CUP_URL, { cache: "no-store" });
    container.innerHTML = await response.text();
  }

  function render(state) {
    const svg = document.querySelector("#cup-stage svg");
    if (!svg) {
      return;
    }

    const color = LIQUID_COLORS[state.baseCode] || "#c9a06a";
    svg.querySelector("#liquid").setAttribute("fill", color);
    svg.querySelector("#liquid-surface").setAttribute("fill", lighten(color, 0.32));

    const scale = SIZE_SCALE[state.sizeCode] || 1;
    svg.querySelector("#cup-all")
      .setAttribute("transform", "translate(120 190) scale(" + scale + ") translate(-120 -190)");

    const extras = state.extras || [];
    Object.entries(TOPPING_IDS).forEach(function (entry) {
      const element = svg.querySelector("#" + entry[1]);
      if (element) {
        element.toggleAttribute("hidden", extras.indexOf(entry[0]) === -1);
      }
    });

    const iced = extras.indexOf("ICED") !== -1;
    const steam = svg.querySelector("#steam");
    steam.dataset.iced = String(iced);
    steam.toggleAttribute("hidden", iced);
  }

  function lighten(hex, amount) {
    const value = parseInt(hex.slice(1), 16);
    const r = (value >> 16) & 255;
    const g = (value >> 8) & 255;
    const b = value & 255;
    const mix = function (channel) {
      return Math.round(channel + (255 - channel) * amount);
    };
    return "rgb(" + mix(r) + ", " + mix(g) + ", " + mix(b) + ")";
  }

  window.CupRender = { mount: mount, render: render };
})();
