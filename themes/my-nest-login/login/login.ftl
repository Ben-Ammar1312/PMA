<!DOCTYPE html>
<html lang="${locale}">
<head>
  <meta charset="utf-8"/>
  <title>Se connecter • ${realm.displayName!realm.name}</title>

  <!-- your compiled Tailwind + Geist CSS -->
  <link rel="stylesheet" href="${url.resourcesPath}/css/styles.css">
  <!-- Inter fallback -->

  <link rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap">

  <style>
    /* make sure the whole viewport is light-grey */
    html, body {
      background-color: #EEEEEE;
      font-family: 'Geist','Inter',sans-serif;
    }
  </style>
</head>

<body class="min-h-screen flex items-center justify-center">
<#include "locale.ftl">
<div
        class="
      bg-white
      border border-gray-200
      shadow-sm
      rounded-xl
      w-full max-w-md
      p-8
      flex flex-col gap-6

    "  style="background-color:#ffffff;">
  <!-- top-right language toggle -->


  <!-- logo, pulled up over the card edge -->
  <div class="flex justify-center -mt-2 mb-4">
    <img src="${url.resourcesPath}/img/logoSmallNoBg.png"
         alt="Company logo"
         width="220" height="100"/>
  </div>

  <!-- heading -->
  <h1 class="text-2xl text-center text-[#465573] font-bold mb-4">
    Bienvenue
  </h1>

  <#if message?? && message.summary?has_content>
  <div class="text-center text-red-600">
    ${message.summary?no_esc}
  </div>
</#if>

<!-- form -->
<form id="kc-form-login"
      action="${url.loginAction}"
      method="post"
      class="flex flex-col gap-4">

  <!-- Email field -->
  <div class="flex flex-col gap-1">
    <label for="username" class="text-[#4A4A4A] font-semibold">
      Email
    </label>
    <div class="relative">
      <div class="absolute left-3 top-1/2 -translate-y-1/2 flex items-center gap-2">
        <!-- mail icon -->
        <svg xmlns="http://www.w3.org/2000/svg"
             class="w-5 h-5 text-gray-500"
             fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <rect x="3" y="5" width="18" height="14" rx="2" stroke-width="2"/>
          <path d="M3 7l9 6 9-6" stroke-width="2"/>
        </svg>
        <div class="h-8.5 w-px bg-gray-300"></div>
      </div>
      <input id="username" name="username" type="text"
             placeholder="exemple@gmail.com"
             class="
                   pl-12 w-full
                   border border-gray-300
                   rounded-md py-2
                   text-base bg-transparent
                   focus:border-[#6985C0] focus:ring-2 focus:ring-[#6985C0]
                   transition-shadow
                 " autofocus/>
    </div>
  </div>

  <!-- Password field -->
  <div class="flex flex-col gap-1">
    <label for="password" class="text-[#4A4A4A] font-semibold">
      Mot de passe
    </label>
    <div class="relative">
      <div class="absolute left-3 top-1/2 -translate-y-1/2 flex items-center gap-2">
        <!-- lock icon -->
        <svg xmlns="http://www.w3.org/2000/svg"
             class="w-5 h-5 text-gray-500"
             fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <rect x="5" y="11" width="14" height="8" rx="2" stroke-width="2"/>
          <path d="M12 16v-2" stroke-width="2"/>
          <path d="M8 11V8a4 4 0 0 1 8 0v3" stroke-width="2"/>
        </svg>
        <div class="h-8.5 w-px bg-gray-300"></div>
      </div>
      <input id="password" name="password" type="password"
             placeholder="********"
             class="
                   pl-12 w-full
                   border border-gray-300
                   rounded-md py-2
                   text-base bg-transparent
                   focus:border-[#6985C0] focus:ring-2 focus:ring-[#6985C0]
                   transition-shadow
                 "/>
    </div>
  </div>

  <!-- Submit button -->
  <div class="flex justify-center">
    <button type="submit"
            class="
                  mt-6 w-40
                  bg-[#6985C0] hover:bg-[#637498]
                  text-white font-semibold
                  rounded-lg py-2
                  transition-colors
                ">
      Se Connecter
    </button>
  </div>

  <!-- Always show this link, pointing to your front-end register page -->
  <div class="flex justify-center space-x-1 text-sm mt-2">
    <span class="text-[#4A4A4A]">Pas de compte ?</span>
    <a href="http://localhost:3000/register"
       class="text-[#6985C0] font-semibold hover:underline">
      Créez-en un !
    </a>
  </div>

</form>
</div>
</body>
</html>