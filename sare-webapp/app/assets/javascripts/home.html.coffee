###
Sentilab SARE: a Sentiment Analysis Research Environment
Copyright (C) 2013 Sabanci University Sentilab
http://sentilab.sabanciuniv.edu

This file is part of SARE.

SARE is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SARE is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SARE. If not, see <http://www.gnu.org/licenses/>.
###

# define reusables
$ = window.jQuery
jsRoutes = window.jsRoutes
JSON = window.JSON
Sare = window.Sare
Page = Sare.Page
Helpers = Sare.Helpers
Selectors = Page.Selectors
Selectors.contentContainer = "#ctr-content"
Selectors.signonButton = "#btn-signon"
Selectors.guestSignonButton = "#btn-guest"

$ ->
  init = ->
    $(Selectors.signonButton)
      .tooltip()
    $(Selectors.guestSignonButton)
      .tooltip()

  init()
  
  $(Selectors.contentContainer).on "click", Selectors.signonButton,
    (e) ->
      e.preventDefault()
      
  $(Selectors.contentContainer).on "click", Selectors.guestSignonButton,
    (e) ->
#      e.preventDefault()
#      $(Selectors.guestSignonButton).tooltip "destroy"
#      $(Selectors.contentContainer).loadHtml
#        revert: init
#        route: $(e.target).attr "href"
