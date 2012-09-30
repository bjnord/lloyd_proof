class CorrectionController < ApplicationController
  def index
    @corrections = Correction.all
  end

  def sync
    @statuses = params[:corrections].collect {|c| sync_one(c) }
    respond_to do |format|
      format.json { render :json => @statuses }
    end
  end

protected

  def sync_one(correction_hash)
    begin
      corr = Correction.new(correction_hash)
      return {:status => :ok} if corr.save
      return {:status => :error, :errors => corr.errors.full_messages}
    rescue ActiveModel::MassAssignmentSecurity::Error => e
      return {:status => :error, :errors => [e.message]}
    end
  end
end
