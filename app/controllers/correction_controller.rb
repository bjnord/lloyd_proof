class CorrectionController < ApplicationController
  def index
    @corrections = Correction.all
  end
end
